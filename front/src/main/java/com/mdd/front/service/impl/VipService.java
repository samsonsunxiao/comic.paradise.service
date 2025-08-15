package com.mdd.front.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.common.entity.setting.DevPayConfig;
import com.mdd.common.entity.setting.DevPayWay;
import com.mdd.common.entity.user.User;
import com.mdd.common.entity.user.UserVip;
import com.mdd.common.entity.vip.LevelPay;
import com.mdd.common.entity.vip.LevelPayVo;
import com.mdd.common.entity.vip.LevelRights;
import com.mdd.common.entity.vip.LevelRightsVo;
import com.mdd.common.entity.vip.PayModel;
import com.mdd.common.entity.vip.VipLevel;
import com.mdd.common.entity.vip.VipLevelDetailVo;
import com.mdd.common.entity.vip.VipRights;
import com.mdd.common.enums.PaymentEnum;
import com.mdd.common.enums.YesNoEnum;
import com.mdd.common.mapper.setting.DevPayWayMapper;
import com.mdd.common.mapper.user.UserMapper;
import com.mdd.common.mapper.user.UserVipMapper;
import com.mdd.common.mapper.vip.LevelPayMapper;
import com.mdd.common.mapper.vip.LevelRightsMapper;
import com.mdd.common.mapper.vip.PaymodelMapper;
import com.mdd.common.mapper.vip.VipLevelMapper;
import com.mdd.front.service.IPayService;
import com.mdd.front.service.IRechargeService;
import com.mdd.front.service.IVipService;
import com.mdd.front.validate.VipPayVaildate;

import lombok.extern.slf4j.Slf4j;

import com.mdd.front.validate.RechargeValidate;
import com.mdd.front.validate.PaymentValidate;
import java.math.BigDecimal;
import java.util.*;

import javax.annotation.Resource;

@Slf4j
@Service
public class VipService implements IVipService {
        @Autowired
        @Resource
        UserMapper userMapper;

        @Autowired
        @Resource
        UserVipMapper userVipMapper;

        @Autowired
        @Resource
        VipLevelMapper vipLevelMapper;

        @Autowired
        @Resource
        LevelRightsMapper levelRightsMapper;

        @Autowired
        @Resource
        LevelPayMapper levelPayMapper;

        @Autowired
        @Resource
        PaymodelMapper paymodelMapper;

        @Resource
        IRechargeService iRechargeService;

        @Resource
        DevPayWayMapper devPayWayMapper;

        @Resource
        IPayService iPayService;

        @Override
        public List<VipLevelDetailVo> listVip() {
                MPJQueryWrapper<VipLevel> mpjQueryWrapper = new MPJQueryWrapper<VipLevel>()
                                .selectAll(VipLevel.class);
                IPage<VipLevel> iPage = vipLevelMapper.selectJoinPage(
                                new Page<>(0, -1),
                                VipLevel.class,
                                mpjQueryWrapper);
                List<VipLevelDetailVo> list = new LinkedList<>();
                for (VipLevel item : iPage.getRecords()) {
                        VipLevelDetailVo vo = new VipLevelDetailVo();
                        BeanUtils.copyProperties(item, vo);

                        MPJQueryWrapper<LevelRights> mpjQueryWrapper1 = new MPJQueryWrapper<LevelRights>()
                                        .selectAll(LevelRights.class)
                                        .select("r.title as title, r.descript as descript,r.icon as icon")
                                        .innerJoin("xmod_vip_rights r ON r.keyid=t.rights")
                                        .eq("t.level", item.getKeyid());
                        IPage<LevelRightsVo> iPage1 = levelRightsMapper.selectJoinPage(
                                        new Page<>(0, -1),
                                        LevelRightsVo.class,
                                        mpjQueryWrapper1);

                        List<VipRights> listRights = new ArrayList<>();
                        for (LevelRightsVo item1 : iPage1.getRecords()) {
                                VipRights rights = new VipRights();
                                rights.setTitle(item1.getTitle());
                                rights.setDescript(item1.getDescript());
                                rights.setIcon(item1.getIcon());
                                rights.setKeyid(item1.getRights());
                                listRights.add(rights);
                        }
                        vo.setRights(listRights);
                        // 支付模式列表
                        MPJQueryWrapper<LevelPay> mpjQueryWrapper2 = new MPJQueryWrapper<LevelPay>()
                                        .selectAll(LevelPay.class)
                                        .select("p.title as title, p.price as price, p.image as image")
                                        .innerJoin("xmod_pay_model p ON p.keyid=t.paymodel")
                                        .eq("t.level", item.getKeyid())
                                        .orderByAsc("p.price");
                        IPage<LevelPayVo> iPage2 = levelPayMapper.selectJoinPage(
                                        new Page<>(0, -1),
                                        LevelPayVo.class,
                                        mpjQueryWrapper2);

                        List<PayModel> listPays = new ArrayList<>();
                        for (LevelPayVo item2 : iPage2.getRecords()) {
                                PayModel payModel = new PayModel();
                                payModel.setTitle(item2.getTitle());
                                payModel.setKeyid(item2.getPaymodel());
                                payModel.setPrice(item2.getPrice());
                                payModel.setImage(item2.getImage());
                                listPays.add(payModel);
                        }
                        vo.setPays(listPays);
                        list.add(vo);
                }
                return list;
        }

        private BigDecimal getVipPaymentAmount(VipPayVaildate vipPayVaildate, Integer userId) {
                User user = userMapper.selectOne(new QueryWrapper<User>()
                                .select("id,nickname")
                                .eq("id", userId)
                                .eq("is_disable", 0)
                                .last("limit 1"));
                Assert.notNull(user, "用户不存在");
                // UserVip userVip = userVipMapper.selectOne(new QueryWrapper<UserVip>()
                //                 .eq("user_id", userId)
                //                 .last("limit 1"));
                // if (userVip != null) {
                //         Assert.isTrue(userVip.getVip() <= vipPayVaildate.getVip(), "当前会员等级不可降级");
                //         if (userVip.getVip() == vipPayVaildate.getVip()) {
                //                 if (userVip.getPayModel().startsWith("pay-quarter")) {
                //                         Assert.isTrue(vipPayVaildate.getPay().startsWith("pay-month"), "当前支付不能降级");
                //                 }
                //                 else if (userVip.getPayModel().startsWith("pay-year")) {
                //                         Assert.isTrue(vipPayVaildate.getPay().startsWith("pay-month")
                //                                         || vipPayVaildate.getPay().startsWith("pay-quarter"),
                //                                         "当前支付不能降级");
                //                 }
                //         }
                // }
                VipLevel vipLevel = vipLevelMapper.selectOne(new QueryWrapper<VipLevel>()
                                .eq("value", vipPayVaildate.getVip())
                                .last("limit 1"));
                Assert.notNull(vipLevel, "等级不存在");
                LevelPay levelPay = levelPayMapper.selectOne(new QueryWrapper<LevelPay>()
                                .eq("level", vipLevel.getKeyid())
                                .eq("paymodel", vipPayVaildate.getPay())
                                .last("limit 1"));
                Assert.notNull(levelPay, "支付模式不存在");
                PayModel payModel = paymodelMapper.selectOne(new QueryWrapper<PayModel>()
                                .eq("keyid", vipPayVaildate.getPay())
                                .last("limit 1"));
                Assert.notNull(payModel, "支付模式不存在");
                return payModel.getPrice();

        }

        public Object getPayQr(VipPayVaildate VipPayVaildate, Integer userId, Integer terminal) {
                // 取公众号支付方式
                List<DevPayWay> devPayWays = devPayWayMapper.selectList(
                                new QueryWrapper<DevPayWay>()
                                                .eq("scene", terminal)
                                                .eq("status", YesNoEnum.YES.getCode()).orderByDesc("id"));
                log.info("getPayQr VipPayVaildate:{}", VipPayVaildate);
                BigDecimal amount = getVipPaymentAmount(VipPayVaildate, userId);
                List<Map<String, Object>> listWay = new ArrayList<>();
                for (DevPayWay way : devPayWays) {
                        // if (!way.getPayConfigId().equals(PaymentEnum.ALI_PAY.getCode())) {
                        //         continue;
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
                        Integer orderId = (Integer) orderMap.get("order_id");
                        PaymentValidate paymentValidate = new PaymentValidate();
                        paymentValidate.setUserId(userId);
                        paymentValidate.setOrderId(orderId);
                        paymentValidate.setScene("vip");
                        paymentValidate.setPayWay(way.getPayConfigId());
                        paymentValidate.setOrderAmount(amount);
                        paymentValidate.setDescription("开通会员");
                        paymentValidate.setOutTradeNo(orderMap.get("order_sn").toString());
                        paymentValidate.setParam(VipPayVaildate.getVip());
                        paymentValidate.setChannel(VipPayVaildate.getSupply());
                        String tagid = "";
                        if (VipPayVaildate.getFile() != null && !VipPayVaildate.getFile().isEmpty()){
                            String[] parts = VipPayVaildate.getFile().split("_"); // 按"_"分割字符串
                            if (parts.length >= 2){
                                tagid = parts[parts.length - 2]; // 获取倒数第二个元素
                            }
                        }
                        paymentValidate.setTagid(tagid);
                        Object payResult = iPayService.prepay(paymentValidate, terminal, "");
                        mapWay.put("result", payResult);
                        listWay.add(mapWay);
                }
                return listWay;
        }
}
