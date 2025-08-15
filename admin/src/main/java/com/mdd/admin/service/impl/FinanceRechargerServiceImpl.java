package com.mdd.admin.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.binarywang.wxpay.bean.result.WxPayRefundV3Result;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.admin.service.IFinanceRechargerService;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.finance.FinanceRechargeSearchValidate;
import com.mdd.admin.vo.finance.FinanceRechargeListExportVo;
import com.mdd.admin.vo.finance.FinanceRechargeListVo;
import com.mdd.admin.vo.user.UserListExportVo;
import com.mdd.admin.vo.user.UserVo;
import com.mdd.common.config.AlipayConfig;
import com.mdd.common.config.GlobalConfig;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.AppPay;
import com.mdd.common.entity.RechargeOrder;
import com.mdd.common.entity.RefundLog;
import com.mdd.common.entity.RefundRecord;
import com.mdd.common.entity.user.User;
import com.mdd.common.enums.LogMoneyEnum;
import com.mdd.common.enums.PaymentEnum;
import com.mdd.common.enums.RefundEnum;
import com.mdd.common.exception.OperateException;
import com.mdd.common.mapper.AppPayMapper;
import com.mdd.common.mapper.RechargeOrderMapper;
import com.mdd.common.mapper.RefundLogMapper;
import com.mdd.common.mapper.RefundRecordMapper;
import com.mdd.common.mapper.log.UserAccountLogMapper;
import com.mdd.common.mapper.user.UserMapper;
import com.mdd.common.plugin.wechat.WxPayDriver;
import com.mdd.common.plugin.wechat.request.RefundRequestV3;
import com.mdd.common.util.*;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 充值记录服务实现类
 */
@Service
public class FinanceRechargerServiceImpl implements IFinanceRechargerService {

    @Resource
    RechargeOrderMapper rechargeOrderMapper;

    @Resource
    UserMapper userMapper;

    @Resource
    UserAccountLogMapper userAccountLogMapper;

    @Resource
    RefundRecordMapper refundRecordMapper;

    @Resource
    RefundLogMapper refundLogMapper;

    @Resource
    DataSourceTransactionManager transactionManager ;

    @Resource
    TransactionDefinition transactionDefinition;

    @Resource
    AppPayMapper appPayMapper;
    /**
     * 充值记录
     *
     * @author fzr
     * @param pageValidate 分页参数
     * @param searchValidate 搜索参数
     * @return PageResult<FinanceRechargeListVo>
     */
    @Override
    public PageResult<FinanceRechargeListVo> list(PageValidate pageValidate, FinanceRechargeSearchValidate searchValidate) {
        Integer pageNo = pageValidate.getPage_no();
        Integer pageSize = pageValidate.getPage_size();

        MPJQueryWrapper<RechargeOrder> mpjQueryWrapper = new MPJQueryWrapper<>();
        mpjQueryWrapper.selectAll(RechargeOrder.class)
                .select("U.id as user_id,U.account ,U.nickname,U.avatar, t.sn AS sn")
                .leftJoin("?_user U ON U.id=t.user_id".replace("?_", GlobalConfig.tablePrefix))
                .orderByDesc("id");

        rechargeOrderMapper.setSearch(mpjQueryWrapper, searchValidate, new String[]{
                "like:sn@t.sn:str",
                "=:pay_way@t.pay_way:int",
                "=:channel@t.channel:str",
                "=:pay_status@t.pay_status:int",
                "datetime:start_time-end_time@t.create_time:str",
        });

        if (StringUtils.isNotEmpty(searchValidate.getUser_info())) {
            String keyword = searchValidate.getUser_info();
            mpjQueryWrapper.nested(wq->wq
                    .like("U.nickname", keyword).or()
                    .like("U.account", keyword).or()
                    .like("U.sn", keyword).or()
                    .like("U.mobile", keyword));
        }

        IPage<FinanceRechargeListVo> iPage = rechargeOrderMapper.selectJoinPage(
                new Page<>(pageNo, pageSize),
                FinanceRechargeListVo.class,
                mpjQueryWrapper);

        for (FinanceRechargeListVo vo : iPage.getRecords()) {
            vo.setCreateTime(TimeUtils.timestampToDate(vo.getCreateTime()));
            vo.setPayTime(StringUtils.isNull(vo.getPayTime()) ? "-" : TimeUtils.timestampToDate(vo.getPayTime()));
            vo.setAvatar(UrlUtils.toAdminAbsoluteUrl(vo.getAvatar()));
            vo.setPayWay(vo.getPayWay());
            vo.setPayWayText(PaymentEnum.getPayWayMsg(Integer.parseInt(vo.getPayWay())));
            vo.setPayStatusText(PaymentEnum.getPayStatusMsg(vo.getPayStatus()));
        }

        return PageResult.iPageHandle(iPage);
    }

    /**
     * 发起退款
     *
     * @author fzr
     * @param orderId 订单ID
     * @param adminId 管理员ID
     */
    @Override
    public void refund(Integer orderId, Integer adminId, Integer refundRate) {
        RechargeOrder rechargeOrder = rechargeOrderMapper.selectById(orderId);

        Assert.notNull(rechargeOrder, "充值订单不存在!");
        if (!rechargeOrder.getPayStatus().equals(PaymentEnum.OK_PAID.getCode())) {
            throw new OperateException("当前订单不可退款!");
        }

        if (rechargeOrder.getRefundStatus().equals(1)) {
            throw new OperateException("订单已发起退款,退款失败请到退款记录重新退款!");
        }
        Integer userId = rechargeOrder.getUserId();
        User user = null;
        if (userId != 0) {
            user = userMapper.selectById(rechargeOrder.getUserId());
            if (user.getUserMoney().compareTo(rechargeOrder.getOrderAmount()) < 0) {
                throw new OperateException("退款失败:用户余额已不足退款金额!");
            }
        }

        // 开启事务
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        RefundRecord refundRecord = null;
        RefundLog log = null;
        try {
            // 标记退款状态
            rechargeOrder.setRefundStatus(1);
            rechargeOrderMapper.updateById(rechargeOrder);
            if (user != null) {
            // 记录余额日志
                userAccountLogMapper.dec(
                        user.getId(),
                        LogMoneyEnum.UM_DEC_RECHARGE.getCode(),
                        rechargeOrder.getOrderAmount(),
                        rechargeOrder.getId(),
                        rechargeOrder.getSn(),
                        "充值订单退款",
                        null
                );
            }


            // 更新用户余额
            if (user != null) {
                user.setUserMoney(user.getUserMoney().subtract(rechargeOrder.getOrderAmount()));
                userMapper.updateById(user);
            }
            BigDecimal refundAmount = rechargeOrder.getOrderAmount().multiply(new BigDecimal(refundRate)).divide(new BigDecimal("100")); // 再除以 100
            // 生成退款记录
            String refundSn = refundRecordMapper.randMakeOrderSn("sn");
            refundRecord = new RefundRecord();
            refundRecord.setSn(refundSn);
            refundRecord.setUserId(rechargeOrder.getUserId());
            refundRecord.setOrderId(rechargeOrder.getId());
            refundRecord.setOrderSn(rechargeOrder.getSn());
            refundRecord.setOrderType(RefundEnum.getOrderType(RefundEnum.ORDER_TYPE_RECHARGE.getCode()));
            refundRecord.setOrderAmount(rechargeOrder.getOrderAmount());
            refundRecord.setRefundRate(refundRate);
            refundRecord.setRefundAmount(refundAmount);
            refundRecord.setRefundType(RefundEnum.TYPE_ADMIN.getCode());
            refundRecord.setTransactionId(refundRecord.getTransactionId());
            refundRecord.setRefundWay(rechargeOrder.getPayWay());
            refundRecordMapper.insert(refundRecord);

            // 生成退款日志
            log = new RefundLog();
            log.setSn(refundLogMapper.randMakeOrderSn("sn"));
            log.setRecordId(refundRecord.getId());
            log.setUserId(rechargeOrder.getUserId());
            log.setHandleId(adminId);
            log.setOrderAmount(rechargeOrder.getOrderAmount());
            log.setRefundAmount(refundRecord.getRefundAmount());
            log.setRefundStatus(RefundEnum.REFUND_ING.getCode());
            log.setCreateTime(System.currentTimeMillis() / 1000);
            log.setUpdateTime(System.currentTimeMillis() / 1000);
            refundLogMapper.insert(log);

            // 发起退款请求
            String refundResponse = null;
            if (rechargeOrder.getPayWay().equals(PaymentEnum.ALI_PAY.getCode())) { //支付宝退款
                refundResponse = this.aliPayRefund(rechargeOrder, refundSn, refundAmount);
            } else if (rechargeOrder.getPayWay().equals(PaymentEnum.PAY_360.getCode())) {
                refundResponse = Refund360(rechargeOrder, refundSn, refundAmount);
            } 
            else { //微信
                refundResponse = this.wxPayRefund(rechargeOrder, refundSn, refundAmount);
            }

            // 退款记录更新
            refundRecord.setRefundStatus(RefundEnum.REFUND_SUCCESS.getCode());
            refundRecord.setCreateTime(System.currentTimeMillis() / 1000);
            refundRecord.setUpdateTime(System.currentTimeMillis() / 1000);
            refundRecord.setTransactionId(rechargeOrder.getTransactionId());
            refundRecordMapper.updateById(refundRecord);

            // 退款日志更新
            log.setRefundStatus(RefundEnum.REFUND_SUCCESS.getCode());
            log.setUpdateTime(System.currentTimeMillis() / 1000);
            if (StringUtils.isNotNull(refundResponse)) {
                log.setRefundMsg(refundResponse);
            }
            refundLogMapper.updateById(log);
            

            transactionManager.commit(transactionStatus);
        } catch (Exception e) {
            // 事务回滚
            transactionManager.rollback(transactionStatus);

            if (refundRecord != null && StringUtils.isNotNull(refundRecord)) {
                refundRecord.setRefundStatus(RefundEnum.REFUND_ERROR.getCode());
                refundRecordMapper.updateById(refundRecord);
            }

            if (log != null && StringUtils.isNotNull(log)) {
                log.setRefundStatus(RefundEnum.REFUND_ERROR.getCode());
                refundLogMapper.updateById(log);
            }
            throw new OperateException(e.getMessage());
        }
    }

    /**
     * 支付宝退款
     * @param rechargeOrder
     * @param refundSn
     */
    private String aliPayRefund(RechargeOrder rechargeOrder, String refundSn, BigDecimal refundAmount) throws Exception {
        String gateWay = StringUtils.isNotNull(YmlUtils.get("like.alidebug")) && YmlUtils.get("like.alidebug").equals("true") ? AlipayConfig.GATEWAY_URL_DEBUG : AlipayConfig.GATEWAY_URL;
        AlipayClient alipayClient = new DefaultAlipayClient(gateWay, ConfigUtils.getAliDevPay("app_id"), ConfigUtils.getAliDevPay("private_key"), "json", AlipayConfig.CHARSET, ConfigUtils.getAliDevPay("ali_public_key"), AlipayConfig.SIGN_TYPE);
        AlipayTradeRefundRequest aliRequest = new AlipayTradeRefundRequest();
        AlipayTradeRefundModel alipayTradeRefundModel = new AlipayTradeRefundModel();
        alipayTradeRefundModel.setTradeNo(rechargeOrder.getTransactionId());
        //alipayTradeRefundModel.setRefundAmount(rechargeOrder.getOrderAmount().toString());
        alipayTradeRefundModel.setRefundAmount(refundAmount.toString());
        alipayTradeRefundModel.setOutRequestNo(rechargeOrder.getSn());
        aliRequest.setBizModel(alipayTradeRefundModel);
        AlipayTradeRefundResponse response = alipayClient.execute(aliRequest);
        if(response.isSuccess()) {
            String ret = JSONObject.toJSONString(response);
            if (StringUtils.isNotNull(ret)) {
                JSONObject refundResponseJSON = JSONObject.parseObject(ret);
                rechargeOrder.setRefundTransactionId(refundResponseJSON.getBigInteger("tradeNo"));
                rechargeOrderMapper.updateById(rechargeOrder);
            }
            return ret;
        } else {
            throw new Exception(response.getBody());
        }
    }

    /**
     * w微信退款
     * @param rechargeOrder
     * @param refundSn
     * @throws WxPayException
     */
    private String wxPayRefund(RechargeOrder rechargeOrder, String refundSn, BigDecimal refundAmount) throws WxPayException {
        String refundTransactionId = null;
        RefundRequestV3 requestV3 = new RefundRequestV3();
        requestV3.setTransactionId(rechargeOrder.getTransactionId());
        requestV3.setOutTradeNo(rechargeOrder.getSn());
        requestV3.setOutRefundNo(refundSn);
        requestV3.setTotalAmount(AmountUtil.yuan2Fen(rechargeOrder.getOrderAmount().toString()));
        requestV3.setRefundAmount(AmountUtil.yuan2Fen(refundAmount.toString()));
        //requestV3.setRefundAmount(AmountUtil.yuan2Fen(rechargeOrder.getOrderAmount().toString()));
        WxPayRefundV3Result result = WxPayDriver.refund(requestV3);

        refundTransactionId = result.getTransactionId();
        rechargeOrder.setRefundTransactionId(new BigInteger(refundTransactionId));
        rechargeOrderMapper.updateById(rechargeOrder);

        return JSONObject.toJSONString(result);
    }

    private String build360Sign(Map<String, String> paramMap, String salt) {
        // 移除sign参数
        paramMap.remove("sign");
        
        // 过滤空值并收集键
        List<String> keys = new ArrayList<>();
        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                keys.add(entry.getKey());
            }
        }
        
        // 按键排序
        Collections.sort(keys);
        
        // 构建参数字符串
        StringBuilder paramStrBuilder = new StringBuilder();
        for (String key : keys) {
            paramStrBuilder.append(key)
                          .append("=")
                          .append(paramMap.get(key))
                          .append("&");
        }
        
        // 移除最后的&并添加salt
        String paramStr = paramStrBuilder.toString();
        if (paramStr.endsWith("&")) {
            paramStr = paramStr.substring(0, paramStr.length() - 1);
        }
        paramStr += salt;
        
        // 计算MD5
        String md5Str = ToolUtils.makeMd5(paramStr);
        return md5Str;
    }

    private String Refund360(RechargeOrder rechargeOrder, String refundSn, BigDecimal refundAmount) throws Exception {
        String appid = YmlUtils.get("ly.360.appid");
        long qid = Long.parseLong(YmlUtils.get("ly.360.qid"));
        String appsecret = YmlUtils.get("ly.360.secret");
        //先申请授权
        String accessUrl = "http://api.openstore.360.cn/main/open/v1/auth/access_token";
        Map<String, Object> authParam = new HashMap<>();
        authParam.put("appid", appid);
        authParam.put("qid", qid);
        authParam.put("appsecret", appsecret);
        authParam.put("timestamp", System.currentTimeMillis() / 1000);
        String result = HttpUtils.sendPost(accessUrl, JSONObject.toJSONString(authParam));
        JSONObject jsonResult = new JSONObject();
        jsonResult = JSON.parseObject(result);
        if (StringUtils.isNull(jsonResult) || Integer.parseInt(jsonResult.get("errno").toString()) != 0) {
            throw new OperateException("360退款access_token失败");
        }
        JSONObject dataObj = jsonResult.getJSONObject("data");
        if (StringUtils.isNull(dataObj)) {
            throw new OperateException("360退款access_token data失败");
        }
        AppPay appPay = appPayMapper.selectOne(new QueryWrapper<AppPay>()
                .eq("order_sn", rechargeOrder.getSn())
                .last("limit 1"));
        Assert.notNull(appPay, "无效支付订单！");
        String accessToken = dataObj.getString("access_token");
        Long timestamp = System.currentTimeMillis() / 1000;
        Map<String, String> param = new HashMap<>();
        param.put("appid", appid);
        param.put("qid", String.format("%d", qid));
        param.put("access_token", accessToken);
        param.put("timestamp", String.format("%d", timestamp));
        param.put("order_id", rechargeOrder.getSn());
        param.put("order_amount", String.format("%d", AmountUtil.yuan2Fen(refundAmount.toString())));
        param.put("user_id", appPay.getGuid());
        param.put("refund_reason", "用户申请退款");
        String sign = build360Sign(param, appsecret);
        JSONObject params = new JSONObject();
        params.put("appid", appid);
        params.put("qid", qid);
        params.put("timestamp", timestamp);
        params.put("access_token", accessToken);
        params.put("sign", sign);
        params.put("order_id", rechargeOrder.getSn());
        params.put("order_amount",  AmountUtil.yuan2Fen(refundAmount.toString()));
        params.put("user_id", appPay.getGuid());
        params.put("refund_reason", "用户申请退款");
        String queryUrl = "http://api.openstore.360.cn/main/open/v1/order/refund";
        result = HttpUtils.sendPost(queryUrl, params.toJSONString());
        return result;
    }

    /**
     * 重新退款
     *
     * @author fzr
     * @param recordId 记录ID
     * @param adminId 管理员ID
     */
    @Override
    public void refundAgain(Integer recordId, Integer adminId) {
        // 开启事务
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        RefundLog log = null;
        try {
            RefundRecord refundRecord = refundRecordMapper.selectById(recordId);
            RechargeOrder rechargeOrder = rechargeOrderMapper.selectById(refundRecord.getOrderId());

            Assert.notNull(rechargeOrder, "充值订单丢失!");

            log = refundLogMapper.selectOne(new QueryWrapper<RefundLog>()
                    .eq("record_id", recordId)
                    .last("limit 1"));

            log.setRefundStatus(RefundEnum.REFUND_ING.getCode());
            refundLogMapper.updateById(log);

            // 发起退款请求
            RefundRequestV3 requestV3 = new RefundRequestV3();
            requestV3.setTransactionId(refundRecord.getTransactionId());
            requestV3.setOutTradeNo(refundRecord.getOrderSn());
            requestV3.setOutRefundNo(refundRecord.getSn());
            requestV3.setTotalAmount(AmountUtil.yuan2Fen(rechargeOrder.getOrderAmount().toString()));
            requestV3.setRefundAmount(AmountUtil.yuan2Fen(refundRecord.getOrderAmount().toString()));
            WxPayDriver.refund(requestV3);

            log.setRefundStatus(RefundEnum.REFUND_SUCCESS.getCode());
            refundLogMapper.updateById(log);
            transactionManager.commit(transactionStatus);
        } catch (Exception e) {
            transactionManager.rollback(transactionStatus);
            if (StringUtils.isNotNull(log)) {
                log.setRefundStatus(RefundEnum.REFUND_ERROR.getCode());
                refundLogMapper.updateById(log);
            }
            throw new OperateException(e.getMessage());
        }
    }

    @Override
    public JSONObject getExportData(PageValidate pageValidate, FinanceRechargeSearchValidate searchValidate) {
        Integer page  = pageValidate.getPage_no();
        Integer limit = pageValidate.getPage_size();
        PageResult<FinanceRechargeListVo> userVoPageResult = this.list(pageValidate, searchValidate);
        JSONObject ret  = ToolUtils.getExportData(userVoPageResult.getCount(), limit, searchValidate.getPage_start(), searchValidate.getPage_end(),"充值列表");
        return ret;
    }

    @Override
    public String export(FinanceRechargeSearchValidate searchValidate) {
        PageValidate pageValidate = new PageValidate();
        if (StringUtils.isNotNull(searchValidate.getPage_start())) {
            pageValidate.setPage_no(searchValidate.getPage_start());
        } else {
            pageValidate.setPage_no(1);
        }

        if (StringUtils.isNotNull(searchValidate.getPage_end()) && StringUtils.isNotNull(searchValidate.getPage_size())) {
            pageValidate.setPage_size(searchValidate.getPage_end() * searchValidate.getPage_size());
        } else {
            pageValidate.setPage_size(20);
        }
        Boolean isAll = StringUtils.isNull(searchValidate.getPage_type()) || searchValidate.getPage_type().equals(0) ? true : false;
        List<FinanceRechargeListExportVo> excellist = this.getExcellist(isAll, pageValidate, searchValidate);
        String fileName = StringUtils.isNull(searchValidate.getFile_name()) ? ToolUtils.makeUUID() : searchValidate.getFile_name();
        String folderPath = "/excel/export/"+ TimeUtils.timestampToDay(System.currentTimeMillis() / 1000) +"/" ;
        String path =  folderPath +  fileName +".xlsx";
        String filePath =  YmlUtils.get("app.upload-directory") + path;
        File folder = new File(YmlUtils.get("app.upload-directory") + folderPath);
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                throw new OperateException("创建文件夹失败");
            }
        }
        EasyExcel.write(filePath)
                .head(FinanceRechargeListExportVo.class)
                .excelType(ExcelTypeEnum.XLSX)
                .sheet("充值记录")
                .doWrite(excellist);
        return UrlUtils.toAdminAbsoluteUrl(path);
    }

    private List<FinanceRechargeListExportVo> getExcellist(boolean isAll, PageValidate pageValidate, FinanceRechargeSearchValidate searchValidate) {
        Integer pageNo = pageValidate.getPage_no();
        Integer pageSize = pageValidate.getPage_size();

        MPJQueryWrapper<RechargeOrder> mpjQueryWrapper = new MPJQueryWrapper<>();
        mpjQueryWrapper.selectAll(RechargeOrder.class)
                .select("U.id as user_id,U.account,U.nickname,U.avatar,t.sn AS sn")
                .leftJoin("?_user U ON U.id=t.user_id".replace("?_", GlobalConfig.tablePrefix))
                .orderByDesc("id");

        rechargeOrderMapper.setSearch(mpjQueryWrapper, searchValidate, new String[]{
                "like:sn@t.sn:str",
                "=:pay_way@t.pay_way:int",
                "=:pay_status@t.pay_status:int",
                "datetime:start_time-end_time@create_time:long",
        });

        if (StringUtils.isNotEmpty(searchValidate.getUser_info())) {
            String keyword = searchValidate.getUser_info();
            mpjQueryWrapper.nested(wq->wq
                    .like("U.nickname", keyword).or()
                    .like("U.sn", keyword).or()
                    .like("U.mobile", keyword));
        }
        List<FinanceRechargeListExportVo> retList = new ArrayList<>();
        if (isAll) {
            retList = rechargeOrderMapper.selectJoinList(FinanceRechargeListExportVo.class, mpjQueryWrapper);
        } else {
            IPage<FinanceRechargeListExportVo> iPage = rechargeOrderMapper.selectJoinPage(
                    new Page<>(pageNo, pageSize),
                    FinanceRechargeListExportVo.class,
                    mpjQueryWrapper);
            retList = iPage.getRecords();
        }
        for (FinanceRechargeListExportVo vo : retList) {
            vo.setCreateTime(TimeUtils.timestampToDate(vo.getCreateTime()));
            vo.setPayTime(TimeUtils.timestampToDate(vo.getPayTime()));
            vo.setAvatar(UrlUtils.toAdminAbsoluteUrl(vo.getAvatar()));
            vo.setPayWay(vo.getPayWay());
            vo.setPayWayText(PaymentEnum.getPayWayMsg(Integer.parseInt(vo.getPayWay())));
            vo.setPayStatusText(PaymentEnum.getPayStatusMsg(vo.getPayStatus()));
        }
        return retList;
    }
}
