package com.mdd.front.controller;

import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import com.mdd.front.FrontThreadLocal;
import com.mdd.front.service.IRechargeService;
import com.mdd.front.validate.RechargeValidate;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.vo.RechargeConfigVo;
import com.mdd.front.vo.RechargeRecordVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/api/recharge")
@Api(tags = "充值管理")
public class RechargeController {

    @Resource
    IRechargeService iRechargeService;

    @GetMapping("/config")
    @ApiOperation(value = "充值配置")
    public AjaxResult<Object> config() {
        Integer userId = FrontThreadLocal.getUserId();

        RechargeConfigVo vo = iRechargeService.config(userId);
        return AjaxResult.success(vo);
    }

    @GetMapping("/lists")
    @ApiOperation(value = "充值记录")
    public AjaxResult<Object> lists(@Validated PageValidate pageValidate) {
        Integer userId = FrontThreadLocal.getUserId();

        PageResult<RechargeRecordVo> list = iRechargeService.record(userId, pageValidate);
        return AjaxResult.success(list);
    }

    @PostMapping("/recharge")
    @ApiOperation(value = "充值下单")
    public AjaxResult<Object> recharge(@Validated @RequestBody RechargeValidate rechargeValidate) {
        Integer userId = FrontThreadLocal.getUserId();
        Integer terminal = FrontThreadLocal.getTerminal();

        Map<String, Object> result = iRechargeService.placeOrder(userId, terminal, rechargeValidate);
        return AjaxResult.success(result);
    }

}
