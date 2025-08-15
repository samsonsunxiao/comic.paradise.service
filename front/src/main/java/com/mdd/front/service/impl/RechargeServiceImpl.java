package com.mdd.front.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.RechargeOrder;
import com.mdd.common.entity.user.User;
import com.mdd.common.entity.user.UserVip;
import com.mdd.common.entity.vip.LevelRights;
import com.mdd.common.entity.vip.LevelRightsVo;
import com.mdd.common.entity.vip.VipLevel;
import com.mdd.common.enums.AccountLogEnum;
import com.mdd.common.enums.PaymentEnum;
import com.mdd.common.exception.OperateException;
import com.mdd.common.mapper.RechargeOrderMapper;
import com.mdd.common.mapper.log.UserAccountLogMapper;
import com.mdd.common.mapper.user.UserMapper;
import com.mdd.common.mapper.user.UserVipMapper;
import com.mdd.common.mapper.vip.LevelRightsMapper;
import com.mdd.common.mapper.vip.VipLevelMapper;
import com.mdd.common.util.ConfigUtils;
import com.mdd.common.util.StringUtils;
import com.mdd.common.util.TimeUtils;
import com.mdd.front.service.IRechargeService;
import com.mdd.front.validate.RechargeValidate;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.vo.RechargeConfigVo;
import com.mdd.front.vo.RechargeRecordVo;

import lombok.extern.slf4j.Slf4j;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 充值余额服务实现类
 */
@Slf4j
@Service
public class RechargeServiceImpl implements IRechargeService {

    @Resource
    UserAccountLogMapper userAccountLogMapper;
    @Resource
    RechargeOrderMapper rechargeOrderMapper;

    @Resource
    UserMapper userMapper;

    @Resource
    UserVipMapper userVipMapper;

    @Resource
    VipLevelMapper vipLevelMapper;

    @Resource
    LevelRightsMapper levelRightsMapper;

    /**
     * 充值配置
     *
     * @author fzr
     * @param userId 用户ID
     * @return RechargeConfigVo
     */
    @Override
    public RechargeConfigVo config(Integer userId) {
        User user = userMapper.selectById(userId);
        Map<String, String> config = ConfigUtils.get("recharge");

        RechargeConfigVo vo = new RechargeConfigVo();
        vo.setStatus(Integer.parseInt(config.getOrDefault("status", "0")));
        vo.setMinAmount(new BigDecimal(config.getOrDefault("min_amount", "0")));
        vo.setUserMoney(user.getUserMoney());
        return vo;
    }

    /**
     * 充值记录
     *
     * @author fzr
     * @param userId       用户ID
     * @param pageValidate 分页参数
     * @return PageResult<RechargeRecordVo>
     */
    @Override
    public PageResult<RechargeRecordVo> record(Integer userId, PageValidate pageValidate) {
        Integer pageNo = pageValidate.getPage_no();
        Integer pageSize = pageValidate.getPage_size();

        QueryWrapper<RechargeOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("pay_status", PaymentEnum.OK_PAID.getCode());
        queryWrapper.orderByDesc("id");

        IPage<RechargeOrder> iPage = rechargeOrderMapper.selectPage(new Page<>(pageNo, pageSize), queryWrapper);

        List<RechargeRecordVo> list = new LinkedList<>();
        for (RechargeOrder rechargeOrder : iPage.getRecords()) {
            RechargeRecordVo vo = new RechargeRecordVo();
            vo.setId(rechargeOrder.getId());
            vo.setAction(1);
            vo.setOrderAmount(rechargeOrder.getOrderAmount());
            vo.setCreateTime(TimeUtils.timestampToDate(rechargeOrder.getPayTime()));
            vo.setTips("充值" + vo.getOrderAmount() + "元");
            list.add(vo);
        }

        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), list);
    }

    /**
     * 创建充值订单
     *
     * @author fzr
     * @param userId           用户ID
     * @param terminal         设备端
     * @param rechargeValidate 参数
     * @return Map<String, Object>
     */
    @Override
    public Map<String, Object> placeOrder(Integer userId, Integer terminal, RechargeValidate rechargeValidate) {
        if (userId != 0) {
            RechargeConfigVo config = this.config(userId);
            if (config.getStatus().equals(0)) {
                throw new OperateException("充值功能已关闭");
            }

            if (rechargeValidate.getMoney().compareTo(config.getMinAmount()) < 0) {
                throw new OperateException("充值金额不能少于" + config.getMinAmount());
            }
        }
        RechargeOrder order = new RechargeOrder();
        order.setUserId(userId);
        order.setOrderTerminal(terminal);
        order.setSn(rechargeOrderMapper.randMakeOrderSn("sn"));
        order.setPayStatus(0);
        order.setRefundStatus(0);
        order.setOrderAmount(rechargeValidate.getMoney());
        if (rechargeValidate.getPayWay() == null){
            order.setPayWay(2);
        }else{
            order.setPayWay(rechargeValidate.getPayWay());
        }
        order.setCreateTime(System.currentTimeMillis() / 1000);
        order.setUpdateTime(System.currentTimeMillis() / 1000);
        rechargeOrderMapper.insert(order);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("order_id", order.getId());
        response.put("order_sn", order.getSn());
        response.put("create_time", order.getCreateTime());
        response.put("from", "recharge");
        return response;
    }

    @Override
    @Transactional
    public void updatePayOrderStatusToPaid(String outTradeNo, String tradeNo, BigDecimal amount, Object param,
            String channel, String ipstr, String tagid) {
        RechargeOrder order = rechargeOrderMapper
                .selectOne(new QueryWrapper<RechargeOrder>().eq("sn", outTradeNo).isNull("delete_time"));
        if (StringUtils.isNull(order)) {
            return;
        }

        if (order.getPayStatus().equals(PaymentEnum.OK_PAID.getCode())) { // 如果是已支付的状态，则不会再更新
            return;
        }
        Integer userId = order.getUserId();
        // String orderSn = outTradeNo;
        if (userId != 0) {
            User user = userMapper.selectById(order.getUserId());
            user.setUserMoney(user.getUserMoney().add(order.getOrderAmount()));
            user.setTotalRechargeAmount(user.getTotalRechargeAmount().add(order.getOrderAmount()));
            userMapper.updateById(user);
        }

        // add account log
        userAccountLogMapper.add(userId, AccountLogEnum.UM_INC_RECHARGE.getCode(), order.getOrderAmount(),
                order.getId(), order.getSn(), "", "");

        order.setTransactionId(tradeNo);
        order.setPayStatus(PaymentEnum.OK_PAID.getCode());
        order.setPayTime(System.currentTimeMillis() / 1000);
        order.setChannel(channel);
        rechargeOrderMapper.updateById(order);
        if (userId == 0) {
            return;
        }
        // 更新会员状态
        Integer vipValue = (Integer) param;
        if (vipValue == null) {
            return;
        }
        String payModel = "";
        MPJQueryWrapper<VipLevel> mpjQueryWrapper = new MPJQueryWrapper<VipLevel>()
                .selectAll(VipLevel.class)
                .select("m.keyid as paymodel")
                .innerJoin("xmod_level_pay l ON l.level=t.keyid")
                .innerJoin("xmod_pay_model m ON m.keyid=l.paymodel")
                .eq("m.price", amount)
                .eq("t.value", vipValue);
        IPage<UserVip> iPage = vipLevelMapper.selectJoinPage(
                new Page<>(0, -1),
                UserVip.class,
                mpjQueryWrapper);
        if (iPage.getRecords().size() > 0) {
            payModel = iPage.getRecords().get(0).getPayModel();
        } else {
            return;
        }
        MPJQueryWrapper<LevelRights> mpjQueryWrapper1 = new MPJQueryWrapper<LevelRights>()
                .selectAll(LevelRights.class)
                .select("r.value as rightvalue,r.type as righttype")
                .innerJoin("xmod_vip_rights r ON r.keyid=t.rights")
                .innerJoin("xmod_vip_level l ON l.keyid=t.level")
                .eq("l.value", vipValue);
        IPage<LevelRightsVo> iPage1 = levelRightsMapper.selectJoinPage(
                new Page<>(0, -1),
                LevelRightsVo.class,
                mpjQueryWrapper1);
        List<Map<String, BigInteger>> listRights = new LinkedList<>();
        for (LevelRightsVo item1 : iPage1.getRecords()) {
            Map<String, BigInteger> map = new HashMap<>();
            // 流量G转为字节
            if (item1.getRighttype().equals("traffic")) {
                BigInteger traffic = BigInteger.valueOf(item1.getRightvalue()).multiply(BigInteger.valueOf(1024))
                        .multiply(BigInteger.valueOf(1024))
                        .multiply(BigInteger.valueOf(1024));
                map.put(item1.getRighttype(), traffic);
            } else {
                map.put(item1.getRighttype(), BigInteger.valueOf(item1.getRightvalue()));
            }

            listRights.add(map);
        }

        // amount获取会员等级
        UserVip userVip = userVipMapper.selectOne(new QueryWrapper<UserVip>().eq("user_id", userId));
        if (userVip == null) {
            userVip = new UserVip();
            userVip.setUserId(userId);
            userVip.setVip(vipValue);
            userVip.setStatus(1);
            userVip.setPayModel(payModel);
            userVip.setData(JSON.toJSONString(listRights));
            userVip.setExpireTime(getExpireTime(userVip.getExpireTime(), payModel));
            userVipMapper.insert(userVip);
        } else {
            userVip.setVip(vipValue);
            userVip.setStatus(1);
            userVip.setPayModel(payModel);
            userVip.setExpireTime(getExpireTime(userVip.getExpireTime(), payModel));
            userVip.setData(JSON.toJSONString(listRights));
            userVipMapper.update(userVip, new QueryWrapper<UserVip>().eq("user_id", userId));
        }
        //回报给3方上报
        Report3rd(tagid, ipstr, amount);
    }

    private long getExpireTime(long curExpiredTime, String payModel) {
        // 获取当前日期
        LocalDateTime dateTime = LocalDateTime.now();
        if (curExpiredTime != 0) {
            dateTime = Instant.ofEpochMilli(curExpiredTime * 1000L)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            ;
        }

        Integer monthNum = 1;
        if (payModel.startsWith("pay-quarter")) {
            monthNum = 3;
        } else if (payModel.startsWith("pay-year")) {
            monthNum = 12;
        }
        LocalDateTime laterWithTime = dateTime.plusMonths(monthNum);
        long timestampInSeconds = laterWithTime
                .atZone(ZoneId.systemDefault()) // 将其转换为包含时区的ZonedDateTime
                .toEpochSecond(); // 转换为秒级时间戳
        return timestampInSeconds;
    }

    private void Report3rd(String tagid, String ip, BigDecimal amount) {
        log.info("Report3rd tagid: {}, ip: {}, amount: {}", tagid, ip, amount);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String strUrl = String.format("http://sem.masyunrui.com/yr/dataCallBackWyy?productId=1077&ip=%s&types=5&money=%f&tagId=%s", ip, amount, tagid);
            log.info("Report3rd url:%s", strUrl);
            HttpGet request = new HttpGet(strUrl);
            // 添加请求头
            request.addHeader("User-Agent", "Mozilla/5.0");
            HttpResponse response = httpClient.execute(request);
            // 获取响应状态码
            int statusCode = response.getStatusLine().getStatusCode();
            // 获取响应内容
            String responseBody = EntityUtils.toString(response.getEntity());
            log.info("Report3rd statusCode:%d responseBody:%s", statusCode, responseBody);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
