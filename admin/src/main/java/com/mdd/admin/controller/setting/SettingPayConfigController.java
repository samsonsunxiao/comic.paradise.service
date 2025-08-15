package com.mdd.admin.controller.setting;


import com.alibaba.fastjson2.JSONObject;
import com.mdd.admin.service.ISettingPaymentService;
import com.mdd.admin.validate.setting.SettingPayConfigValidate;
import com.mdd.admin.vo.setting.SettingPaymentMethodVo;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.entity.setting.DevPayConfig;
import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/adminapi/setting.pay.pay_config")
@Api(tags = "配置支付参数")
public class SettingPayConfigController {

    @Resource
    ISettingPaymentService iSettingPaymentService;

    @GetMapping("/lists")
    @ApiOperation(value="支付配置列表")
    public AjaxResult<JSONObject> list() {
        JSONObject result = iSettingPaymentService.list();
        return AjaxResult.success(result);
    }

    @GetMapping("/getConfig")
    @ApiOperation(value="支付配置详情")
    public AjaxResult<Object> getConfig(@Validated @IDMust() @RequestParam("id") Integer id) {
        DevPayConfig vo = iSettingPaymentService.getConfig(id);
        return AjaxResult.success(vo);
    }

    @PostMapping("/setConfig")
    @ApiOperation(value="支付配置编辑")
    public AjaxResult<Object> setConfig(@Validated @RequestBody SettingPayConfigValidate configValidate) {
        iSettingPaymentService.setConfig(configValidate);
        return AjaxResult.success();
    }


}
