package com.mdd.front.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mdd.common.aop.NotLogin;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.entity.vip.VipLevelDetailVo;
import com.mdd.front.FrontThreadLocal;
import com.mdd.front.service.IPayService;
import com.mdd.front.service.IRechargeService;
import com.mdd.front.service.IVipService;
import com.mdd.front.validate.VipPayVaildate;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("api/vip")
@Api(tags = "会员用户接口")
public class VipController {
    @Resource
    IVipService iVipService;

    @Resource
    IRechargeService iRechargeService;

    @Resource
    IPayService iPayService;

    @GetMapping("/list")
    @NotLogin
    @ApiOperation(value="获取会员等级")
    public AjaxResult<List<VipLevelDetailVo>> listVip() {
        List<VipLevelDetailVo> list =  iVipService.listVip();
        return AjaxResult.success(list);
    }

    @PostMapping("/pay/getqr")
    @ApiOperation(value="请求VIP支付二维码")
    public AjaxResult<Object> getQr(@Validated VipPayVaildate VipPayVaildate){
        Integer userId = FrontThreadLocal.getUserId();
        Integer terminal = FrontThreadLocal.getTerminal();
        Object payResult = iVipService.getPayQr(VipPayVaildate,userId,terminal);
        return AjaxResult.success(payResult);
    }
    
}
