package com.mdd.front.service;

import com.mdd.common.core.PageResult;
import com.mdd.front.validate.RechargeValidate;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.vo.RechargeConfigVo;
import com.mdd.front.vo.RechargeRecordVo;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 充值余额接口服务类
 */
public interface IRechargeService {

    /**
     * 充值配置
     *
     * @author fzr
     * @param userId 用户ID
     * @return RechargeConfigVo
     */
    RechargeConfigVo config(Integer userId);

    /**
     * 充值记录
     *
     * @author fzr
     * @param userId 用户ID
     * @param pageValidate 分页参数
     * @return PageResult<RechargeRecordVo>
     */
    PageResult<RechargeRecordVo> record(Integer userId, PageValidate pageValidate);

    /**
     * 充值下单
     *
     * @param userId 用户ID
     * @param terminal 总端
     * @param rechargeValidate 充值参数
     * @return Map<String, Object>
     */
    Map<String, Object> placeOrder(Integer userId, Integer terminal, RechargeValidate rechargeValidate);

    /**
     * @notes 阿里支付后更新状态
     * @return array
     * @throws @\think\db\exception\DataNotFoundException
     * @throws @\think\db\exception\DbException
     * @throws @\think\db\exception\ModelNotFoundException
     * @author damonyuan
     */
    void updatePayOrderStatusToPaid(String outTradeNo, String tradeNo, BigDecimal amount, Object param, String channel, String ipstr, String tagid);
}
