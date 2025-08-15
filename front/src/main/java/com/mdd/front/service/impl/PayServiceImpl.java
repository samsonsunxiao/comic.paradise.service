package com.mdd.front.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.common.config.AlipayConfig;
import com.mdd.common.entity.AppPay;
import com.mdd.common.entity.RechargeOrder;
import com.mdd.common.entity.setting.DevPayConfig;
import com.mdd.common.entity.setting.DevPayWay;
import com.mdd.common.entity.user.User;
import com.mdd.common.entity.user.UserAuth;
import com.mdd.common.entity.InvoiceOrder;
import com.mdd.common.enums.*;
import com.mdd.common.exception.OperateException;
import com.mdd.common.exception.PaymentException;
import com.mdd.common.mapper.log.UserAccountLogMapper;
import com.mdd.common.mapper.AppPayMapper;
import com.mdd.common.mapper.InvoiceOrderMapper;
import com.mdd.common.mapper.RechargeOrderMapper;
import com.mdd.common.mapper.setting.DevPayConfigMapper;
import com.mdd.common.mapper.setting.DevPayWayMapper;
import com.mdd.common.mapper.user.UserAuthMapper;
import com.mdd.common.mapper.user.UserMapper;
import com.mdd.common.plugin.wechat.WxMnpDriver;
import com.mdd.common.plugin.wechat.WxPayDriver;
import com.mdd.common.plugin.wechat.request.PaymentRequestV3;
import com.mdd.common.util.*;
import com.mdd.front.service.IPayService;
import com.mdd.front.service.IRechargeService;
import com.mdd.front.validate.InvoiceSearchVaildate;
import com.mdd.front.validate.InvoiceVaildate;
import com.mdd.front.validate.PayVaildate;
import com.mdd.front.validate.PaymentValidate;
import com.mdd.front.validate.RechargeValidate;
import com.mdd.front.vo.pay.AppPayStatusVo;
import com.mdd.front.vo.pay.InvoiceOrderVo;
import com.mdd.front.vo.pay.PayStatusVo;
import com.mdd.front.vo.pay.PayWayInfoVo;
import com.mdd.front.vo.pay.PayWayListVo;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
public class PayServiceImpl implements IPayService {

    @Resource
    UserMapper userMapper;

    @Resource
    UserAuthMapper userAuthMapper;

    @Resource
    DevPayWayMapper devPayWayMapper;

    @Resource
    DevPayConfigMapper devPayConfigMapper;

    @Resource
    RechargeOrderMapper rechargeOrderMapper;

    @Resource
    IRechargeService iRechargeService;

    @Resource
    AppPayMapper appPayMapper;

    @Resource
    InvoiceOrderMapper invoiceOrderMapper;

    @Resource
    UserAccountLogMapper logMoneyMapper;
    @Value("${pay.ali.order-notify-url}")
    private String aliPayNotifyUrl;

    Ly360Service ly360Service;

    /**
     * 支付方式
     *
     * @author fzr
     * @param from     场景
     * @param orderId  订单ID
     * @param terminal 终端
     * @return List<PayWayListedVo>
     */
    @Override
    public PayWayListVo payWay(String from, Integer orderId, Integer terminal) {
        List<DevPayWay> devPayWays = devPayWayMapper.selectList(
                new QueryWrapper<DevPayWay>()
                        .eq("scene", terminal)
                        .eq("status", YesNoEnum.YES.getCode()).orderByDesc("id"));

        PayWayListVo vo = new PayWayListVo();
        if (from.equals("recharge")) {
            RechargeOrder rechargeOrder = rechargeOrderMapper.selectById(orderId);
            vo.setOrderAmount(rechargeOrder.getOrderAmount());
        }

        Integer walletType = PaymentEnum.WALLET_PAY.getCode();
        List<PayWayInfoVo> list = new LinkedList<>();
        for (DevPayWay way : devPayWays) {
            if (from.equals("recharge") && way.getPayConfigId().equals(walletType)) {
                continue;
            }

            DevPayConfig devPayConfig = devPayConfigMapper.selectById(way.getPayConfigId());
            PayWayInfoVo infoVo = new PayWayInfoVo();
            infoVo.setId(devPayConfig.getId());
            infoVo.setName(devPayConfig.getName());
            infoVo.setIcon(UrlUtils.toAbsoluteUrl(devPayConfig.getIcon()));
            infoVo.setIsDefault(way.getIsDefault());
            infoVo.setSort(devPayConfig.getSort());
            infoVo.setRemark(devPayConfig.getRemark());
            infoVo.setPayWay(devPayConfig.getPayWay());
            if (devPayConfig.getPayWay().equals(PaymentEnum.WX_PAY.getCode())) {
                infoVo.setExtra("微信快捷支付");
            }
            if (devPayConfig.getPayWay().equals(PaymentEnum.ALI_PAY.getCode())) {
                infoVo.setExtra("支付宝快捷支付");
            }
            list.add(infoVo);
        }

        Collections.sort(list, Comparator.comparing(PayWayInfoVo::getSort).reversed()
                .thenComparing(Comparator.comparingInt(PayWayInfoVo::getId).reversed()));
        vo.setLists(list);
        return vo;
    }

    /**
     * 订单状态
     *
     * @author fzr
     * @param from    场景
     * @param orderId 订单ID
     * @return PayStatusVo
     */
    @Override
    public PayStatusVo payStatus(String from, Integer orderId) {
        PayStatusVo vo = new PayStatusVo();
        boolean orderExist = false;

        switch (from) {
            case "recharge":
                RechargeOrder rechargeOrder = rechargeOrderMapper.selectById(orderId);
                if (StringUtils.isNotNull(rechargeOrder)) {
                    orderExist = true;
                    vo.setPayStatus(rechargeOrder.getPayStatus());
                    vo.setOrderId(rechargeOrder.getId());
                    JSONObject order = new JSONObject();
                    order.put("order_amount", rechargeOrder.getOrderAmount());
                    order.put("order_sn", rechargeOrder.getSn());
                    order.put("pay_time",
                            StringUtils.isNotNull(rechargeOrder.getPayTime())
                                    ? TimeUtils.timestampToDate(rechargeOrder.getPayTime())
                                    : "");
                    order.put("pay_way", PaymentEnum.getPayWayMsg(rechargeOrder.getPayWay()));
                    vo.setOrder(order);
                }
                break;
            case "order":
                break;
        }

        if (!orderExist) {
            throw new OperateException("订单不存在!");
        }

        return vo;
    }

    public Object getPayQr(PayVaildate PayVaildate, Integer userId, Integer terminal) {
        // 取公众号支付方式
        List<DevPayWay> devPayWays = devPayWayMapper.selectList(
                new QueryWrapper<DevPayWay>()
                        .eq("scene", terminal)
                        .eq("status", YesNoEnum.YES.getCode()).orderByDesc("id"));
        log.info("getPayQr PayVaildate:{}", PayVaildate);
        BigDecimal amount = PayVaildate.getAmount();
        List<Map<String, Object>> listWay = new ArrayList<>();
        for (DevPayWay way : devPayWays) {
            // if (!way.getPayConfigId().equals(PaymentEnum.ALI_PAY.getCode())) {
            // continue;
            // }
            Map<String, Object> mapWay = new HashMap<>();
            mapWay.put("way", way.getPayConfigId());
            mapWay.put("name", PaymentEnum.getPayWayMsg(way.getPayConfigId()));
            mapWay.put("amount", amount);
            // 生成订单
            RechargeValidate rechargeValidate = new RechargeValidate();
            rechargeValidate.setMoney(amount);
            Map<String, Object> orderMap = iRechargeService.placeOrder(userId, terminal,
                    rechargeValidate);
            if (orderMap == null) {
                continue; // 下单失败
            }
            // 写入软件支付关系表
            AppPay appPay = new AppPay();
            appPay.setGuid(PayVaildate.getGuid());
            appPay.setOrderSn(orderMap.get("order_sn").toString());
            appPay.setPid(PayVaildate.getPid());
            appPay.setPayType(PayVaildate.getPayType());
            appPay.setLlm(PayVaildate.getLlm());
            appPayMapper.insert(appPay);

            Integer orderId = (Integer) orderMap.get("order_id");
            PaymentValidate paymentValidate = new PaymentValidate();
            paymentValidate.setUserId(userId);
            paymentValidate.setOrderId(orderId);
            paymentValidate.setScene("pay");
            paymentValidate.setPayWay(way.getPayConfigId());
            paymentValidate.setOrderAmount(amount);
            paymentValidate.setDescription("DS部署充值服务");
            paymentValidate.setOutTradeNo(orderMap.get("order_sn").toString());
            paymentValidate.setParam(PayVaildate.getPid());
            paymentValidate.setChannel(PayVaildate.getSupply());
            paymentValidate.setTagid("");
            Object payResult = prepay(paymentValidate, terminal, "");
            mapWay.put("result", payResult);
            listWay.add(mapWay);
        }
        return listWay;
    }

    public Object generateOrder(PayVaildate PayVaildate, Integer userId, Integer terminal) {
        Integer way = 0;
        if (PayVaildate.getType() == null || PayVaildate.getType().isEmpty()) {
            throw new OperateException("支付方式不能为空");
        }
        Integer amount = 0;
        if (PayVaildate.getType().equals("360")) {
            way = 4;
        }
        // 三方支付只需要创建订单
        Map<String, Object> mapWay = new HashMap<>();
        mapWay.put("way", way);
        mapWay.put("name", PaymentEnum.getPayWayMsg(way));
        mapWay.put("amount", amount);
        // 生成订单
        RechargeValidate rechargeValidate = new RechargeValidate();
        rechargeValidate.setMoney(PayVaildate.getAmount());
        rechargeValidate.setPayWay(way);
        Map<String, Object> orderMap = iRechargeService.placeOrder(userId, terminal,
                rechargeValidate);
        if (orderMap == null) {
            throw new OperateException("下单失败");
        }
        // 写入软件支付关系表
        AppPay appPay = new AppPay();
        appPay.setGuid(PayVaildate.getGuid());
        appPay.setOrderSn(orderMap.get("order_sn").toString());
        appPay.setPid(PayVaildate.getPid());
        appPay.setPayType(PayVaildate.getPayType());
        appPay.setLlm(PayVaildate.getLlm());
        appPay.setType(PayVaildate.getType());
        appPayMapper.insert(appPay);
        if (PayVaildate.getType().equals("360")) {
            amount = AmountUtil.yuan2Fen(PayVaildate.getAmount().toPlainString());
        }
        JSONObject ret = new JSONObject();
        ret.put("type", PayVaildate.getType());
        ret.put("amount", amount);
        ret.put("orderid", orderMap.get("order_sn").toString());
        ret.put("createtime", Integer.parseInt(orderMap.get("create_time").toString()));
        return ret;
    }

    @Override
    public InvoiceOrderVo invoice(InvoiceVaildate invoiceVaildate, Integer userId, Integer terminal) {
        Map<String, String> dataMap = MapUtils.jsonToMap(invoiceVaildate.getData());
        if (StringUtils.isNull(dataMap)) {
            throw new OperateException("发票数据不能为空");
        }
        String orderid = dataMap.get("orderid").toString();
        InvoiceOrder invoiceOrder = invoiceOrderMapper.selectOne(new QueryWrapper<InvoiceOrder>()
                .eq("order_id", orderid)
                .eq("guid", invoiceVaildate.getGuid())
                .last("limit 1"));
        if (invoiceOrder != null) {
            // 已经存在发票
            if (invoiceOrder.getInvoice() != null && !invoiceOrder.getInvoice().isEmpty()) {
                InvoiceOrderVo vo = new InvoiceOrderVo();
                vo.setInvoice(JSONObject.parseObject(invoiceVaildate.getData()));
                vo.setResult(JSONObject.parseObject(invoiceOrder.getInvoice()));
                return vo;
            }
        }
        Integer invoiceType = Integer.parseInt(dataMap.get("type").toString());
        JSONObject dataObj = null;
        if (invoiceVaildate.getType() != null && invoiceVaildate.getType().equals("360")) {
            ly360Service = new Ly360Service();
            JSONObject invoiceData = ly360Service.invoice(dataMap);
            if (StringUtils.isNull(invoiceData) || Integer.parseInt(invoiceData.get("errno").toString()) != 0) {
                return null;
            }
            dataObj = invoiceData.getJSONObject("data");
            Assert.notNull(dataObj, "获取360发票 data");
            if (invoiceType == 1) {
                String sourceId = dataObj.get("source_id").toString();
                if (sourceId.isEmpty()) {
                    return null;
                }
            } else {
                String invoiceNo = dataObj.get("invoice_no").toString();
                if (invoiceNo.isEmpty()) {
                    return null;
                }
            }
        }
        if (dataObj != null) {
            // 写入数据库
            Boolean bNew = false;
            if (invoiceOrder == null) {
                invoiceOrder = new InvoiceOrder();
                invoiceOrder.setOrderId(orderid);
                invoiceOrder.setGuid(invoiceVaildate.getGuid());
                bNew = true;
            }
            invoiceOrder.setPid(invoiceVaildate.getPid());
            invoiceOrder.setSupply(invoiceVaildate.getSupply());
            invoiceOrder.setVersion(invoiceVaildate.getVersion());
            invoiceOrder.setData(invoiceVaildate.getData());
            invoiceOrder.setInvoice(dataObj.toJSONString());
            invoiceOrder.setType(invoiceVaildate.getType());
            invoiceOrder.setStatus(2);

            if (invoiceType == 1) {// 专票的话，等待生成
                invoiceOrder.setStatus(1);
            }
            if (bNew) {
                invoiceOrderMapper.insert(invoiceOrder);
            } else {
                invoiceOrderMapper.updateById(invoiceOrder);
            }
        }
        InvoiceOrderVo vo = new InvoiceOrderVo();
        vo.setInvoice(JSONObject.parseObject(invoiceVaildate.getData()));
        vo.setResult(dataObj);
        return vo;
    }

    @Override
    public InvoiceOrderVo queryInvoice(InvoiceSearchVaildate invoiceSearchVaildate) {
        InvoiceOrder invoiceOrder = invoiceOrderMapper.selectOne(new QueryWrapper<InvoiceOrder>()
                .eq("order_id", invoiceSearchVaildate.getOrderId())
                .eq("guid", invoiceSearchVaildate.getGuid())
                .last("limit 1"));
        // Assert.notNull(invoiceOrder, "发票不存在");
        if (invoiceOrder == null) {
            return null;
        }
        JSONObject dataObj = JSONObject.parseObject(invoiceOrder.getData());
        Integer invoiceType = 0;
        if (dataObj != null) {
            invoiceType = Integer.parseInt(dataObj.getString("type"));
        }
        if (invoiceType == 1) {
            // 去查询360的专票
            JSONObject invoiceObj = JSONObject.parseObject(invoiceOrder.getInvoice());
            String type = invoiceOrder.getType();
            if (type.equals("360") && invoiceObj.get("source_id") != null) {
                String sourceId = invoiceObj.get("source_id").toString();
                ly360Service = new Ly360Service();
                JSONObject invoiceData = ly360Service.querySpecial(sourceId, "1");
                if (StringUtils.isNull(invoiceData) || Integer.parseInt(invoiceData.get("errno").toString()) != 0) {
                    return null;
                }
                invoiceObj = invoiceData.getJSONObject("data");
                if (invoiceObj != null) {
                    if (invoiceObj.get("receipt_url") != null) {
                        // 写入数据库
                        invoiceOrder.setInvoice(invoiceObj.toJSONString());
                        invoiceOrderMapper.updateById(invoiceOrder);
                    }
                }
            }
        }
        InvoiceOrderVo vo = new InvoiceOrderVo();
        vo.setInvoice(JSONObject.parseObject(invoiceOrder.getData()));
        vo.setResult(JSONObject.parseObject(invoiceOrder.getInvoice()));
        return vo;
    }

    /**
     * 发起支付
     *
     * @param params   参数
     * @param terminal 终端
     * @return Object
     */
    public Object prepay(PaymentValidate params, Integer terminal, String code) {
        try {
            params.setTerminal(terminal);
            String openId = null;
            UserAuth userAuth = userAuthMapper.selectOne(new QueryWrapper<UserAuth>()
                    .eq("user_id", params.getUserId())
                    // .eq("terminal", terminal)
                    .last("limit 1"));

            if (StringUtils.isNotNull(userAuth)) {
                openId = userAuth.getOpenid();
            } else {
                if (terminal.intValue() != ClientEnum.PC.getCode()) {
                    if (StringUtils.isNotEmpty(code)) {
                        if (ClientEnum.OA.getCode() == terminal.intValue()) {
                            WxMpService wxMpService = WxMnpDriver.oa();
                            WxOAuth2AccessToken wxOAuth2AccessToken = wxMpService.getOAuth2Service()
                                    .getAccessToken(code);
                            openId = wxOAuth2AccessToken.getOpenId();
                        } else if (ClientEnum.MNP.getCode() == terminal.intValue()) {
                            WxMaService wxMaService = WxMnpDriver.mnp();
                            WxMaJscode2SessionResult sessionResult = wxMaService.getUserService().getSessionInfo(code);
                            openId = sessionResult.getOpenid();
                        }
                    }
                }
            }

            switch (params.getPayWay()) {
                case 1: // 余额支付
                    String attach = params.getAttach();
                    String orderSn = params.getOutTradeNo();
                    this.handlePaidNotify(attach, orderSn, null);
                    return Collections.emptyList();
                case 2: // 微信支付
                    Map<String, Object> map = new HashMap<>();
                    map.put("from", params.getScene());
                    map.put("param", params.getParam());
                    map.put("channel", params.getChannel());
                    map.put("ip", IpUtils.getIpAddress());
                    map.put("tagid", params.getTagid());
                    PaymentRequestV3 requestV3 = new PaymentRequestV3();
                    requestV3.setTerminal(terminal);
                    requestV3.setOpenId(openId);
                    requestV3.setAttach(JSON.toJSONString(map));
                    requestV3.setOutTradeNo(params.getOutTradeNo());
                    requestV3.setOrderAmount(params.getOrderAmount());
                    requestV3.setDescription(params.getDescription());
                    log.info("prepay attach:{}", requestV3.getAttach());
                    Object result = WxPayDriver.unifiedOrder(requestV3);
                    JSONObject ret = new JSONObject();
                    ret.put("qrcode", result);
                    ret.put("pay_way", 2);
                    ret.put("order_sn", params.getOutTradeNo());
                    return ret;
                // return WxPayDriver.unifiedOrder(requestV3);
                case 3: // 支付宝
                    return this.createAliH5Order(params);
            }
        } catch (Exception e) {
            throw new OperateException(e.toString());
        }

        throw new PaymentException("支付发起异常");
    }

    @Override
    public AppPayStatusVo checkPay(PayVaildate PayVaildate, Integer userId, Integer terminal) {

        MPJQueryWrapper<AppPay> mpjQueryWrapper = new MPJQueryWrapper<AppPay>()
                .selectAll(AppPay.class)
                .select("o.order_amount as amount")
                .innerJoin(
                        "la_recharge_order o ON o.sn COLLATE utf8mb4_general_ci =t.order_sn COLLATE utf8mb4_general_ci and o.pay_status=1 and o.refund_status=0")
                .eq("o.user_id", userId)
                .eq("o.order_terminal", terminal)
                .eq("t.pid", PayVaildate.getPid())
                .eq("t.guid", PayVaildate.getGuid())
                .eq("t.pay_type", PayVaildate.getPayType());
        // .eq("t.llm", PayVaildate.getLlm());
        // AppPay model = appPayMapper.selectJoinOne(AppPay.class, mpjQueryWrapper);
        IPage<AppPayStatusVo> iPage = appPayMapper.selectJoinPage(
                new Page<>(0, -1),
                AppPayStatusVo.class,
                mpjQueryWrapper);
        AppPayStatusVo model = iPage.getRecords().size() > 0 ? iPage.getRecords().get(0) : null;
        log.info("checkPay model:{}", model);
        if (model != null) {
            AppPayStatusVo appPayStatusVo = new AppPayStatusVo();
            appPayStatusVo.setPayStatus(1);
            appPayStatusVo.setPayType(model.getPayType());
            appPayStatusVo.setOrderSn(model.getOrderSn());
            appPayStatusVo.setAmount(model.getAmount());
            return appPayStatusVo;
        }
        return null;
    }

    /**
     * 支付回调处理
     *
     * @author fzr
     * @param attach        场景码
     * @param outTradeNo    订单编号
     * @param transactionId 流水号
     */
    @Override
    @Transactional
    public void handlePaidNotify(String attach, String outTradeNo, String transactionId) {
        switch (attach) {
            case "order":
                break;
            case "recharge":
                this.rechargeCallback(outTradeNo, transactionId);
                break;
            case "vip":

                break;
        }
    }

    @Override
    public JSONObject createAliH5Order(PaymentValidate paymentValidate) throws AlipayApiException {
        JSONObject ret = new JSONObject();
        // 订单编号
        String orderSn = "";
        // 订单状态
        Integer orderStatus = 0;
        // 订单金额
        BigDecimal orderAmount = BigDecimal.ZERO;
        // Long orderId = 0L;
        String payReturnUrl = "";
        switch (paymentValidate.getScene()) {
            case "member":
                break;
            case "recharge":
            case "vip":
            case "pay":
                RechargeOrder rechargeOrder = rechargeOrderMapper.selectOne(new QueryWrapper<RechargeOrder>()
                        .eq("id", paymentValidate.getOrderId())
                        .eq("pay_status", PaymentEnum.UN_PAID.getCode())
                        .eq("user_id", paymentValidate.getUserId())
                        .last("limit 1"));
                orderSn = rechargeOrder.getSn();
                orderStatus = rechargeOrder.getPayStatus();
                orderAmount = rechargeOrder.getOrderAmount();
                // orderId = Long.valueOf(rechargeOrder.getId());
                payReturnUrl = UrlUtils.localDomain(paymentValidate.getRedirect());
                Assert.notNull(rechargeOrder, "订单不存在");
                break;
            default:
                throw new OperateException("不支持当前订单类型");
        }

        Assert.isTrue(orderStatus != null && orderStatus == PaymentEnum.UN_PAID.getCode(), "订单非待支付状态,不能创建支付单");

        String gateWay = StringUtils.isNotNull(YmlUtils.get("app.alidebug"))
                && YmlUtils.get("app.alidebug").equals("true") ? AlipayConfig.GATEWAY_URL_DEBUG
                        : AlipayConfig.GATEWAY_URL;
        AlipayClient alipayClient = new DefaultAlipayClient(gateWay, ConfigUtils.getAliDevPay("app_id"),
                ConfigUtils.getAliDevPay("private_key"), "json", AlipayConfig.CHARSET,
                ConfigUtils.getAliDevPay("ali_public_key"), AlipayConfig.SIGN_TYPE);
        // 2、封装 Request
        String form = "";
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderSn);
        bizContent.put("total_amount", orderAmount);
        bizContent.put("subject", paymentValidate.getDescription());
        // bizContent.put("store_id", "xmod");
        // bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        // bizContent.put("app_pay", "Y");
        bizContent.put("passback_params", JSONObject.toJSONString(new JSONObject() {
            {
                put("from", paymentValidate.getScene());
                put("param", paymentValidate.getParam());
                put("channel", paymentValidate.getChannel());
                put("ip", IpUtils.getIpAddress());
                put("tagid", paymentValidate.getTagid());
            }
        }));
        if (paymentValidate.getTerminal().equals(ClientEnum.H5.getCode())
                || paymentValidate.getTerminal().equals(ClientEnum.OA.getCode())) {
            // AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();
            // //alipayRequest.setReturnUrl(payReturnUrl);
            // alipayRequest.setBizContent(bizContent.toString());
            AlipayTradePrecreateRequest alipayRequest = new AlipayTradePrecreateRequest();
            alipayRequest.setBizContent(bizContent.toString());
            String notifyUrl = UrlUtils.getRequestUrl() + aliPayNotifyUrl;
            log.info("alipay notify url: " + notifyUrl);
            alipayRequest.setNotifyUrl(notifyUrl);
            try {
                // form = alipayClient.pageExecute(alipayRequest).getBody();
                AlipayTradePrecreateResponse response = alipayClient.execute(alipayRequest);
                if (response.isSuccess()) {
                    String qrCodeUrl = response.getQrCode(); // 支付二维码链接
                    ret.put("qrcode", qrCodeUrl);
                    ret.put("pay_way", paymentValidate.getPayWay());
                    ret.put("order_sn", paymentValidate.getOutTradeNo());
                } else {
                    throw new PaymentException(response.getSubMsg());
                }
                return ret;
            } catch (Exception e) {
                throw new PaymentException(e.getMessage());
            }
        } else {
            AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
            // alipayRequest.setNotifyUrl("http://y4f8ud.natappfree.cc" + aliPayNotifyUrl);
            alipayRequest.setNotifyUrl(UrlUtils.getRequestUrl() + aliPayNotifyUrl);
            alipayRequest.setReturnUrl(payReturnUrl);
            alipayRequest.setBizContent(bizContent.toString());

            try {
                form = alipayClient.pageExecute(alipayRequest).getBody();
                ret.put("config", form);
                ret.put("pay_way", paymentValidate.getPayWay());
                return ret;
            } catch (Exception e) {
                throw new OperateException(e.getMessage());
            }
        }
    }

    /**
     * 余额充值回调
     *
     * @author fzr
     * @param outTradeNo    订单号
     * @param transactionId 流水号
     */
    private void rechargeCallback(String outTradeNo, String transactionId) {
        for (int i = 0; i <= 0; i++) {
            RechargeOrder rechargeOrder = rechargeOrderMapper.selectOne(
                    new QueryWrapper<RechargeOrder>()
                            .eq("sn", outTradeNo).isNull("delete_time")
                            .last("limit 1"));

            if (StringUtils.isNull(rechargeOrder)) {
                log.error("充值订单不存在: {} : {}", outTradeNo, transactionId);
                break;
            }

            if (rechargeOrder.getPayStatus().equals(PaymentEnum.OK_PAID.getCode())) {
                log.error("充值订单已支付: {} : {}", outTradeNo, transactionId);
                break;
            }

            rechargeOrder.setPayStatus(1);
            rechargeOrder.setTransactionId(transactionId);
            rechargeOrder.setPayTime(System.currentTimeMillis() / 1000);
            rechargeOrder.setUpdateTime(System.currentTimeMillis() / 1000);
            rechargeOrderMapper.updateById(rechargeOrder);

            logMoneyMapper.add(rechargeOrder.getUserId(),
                    AccountLogEnum.UM_INC_RECHARGE.getCode(),
                    rechargeOrder.getOrderAmount(),
                    rechargeOrder.getId(),
                    rechargeOrder.getSn(),
                    "用户充值余额", null);

            User user = userMapper.selectById(rechargeOrder.getUserId());
            user.setUserMoney(user.getUserMoney().add(rechargeOrder.getOrderAmount()));
            user.setUpdateTime(System.currentTimeMillis() / 1000);
            userMapper.updateById(user);
        }
    }
}
