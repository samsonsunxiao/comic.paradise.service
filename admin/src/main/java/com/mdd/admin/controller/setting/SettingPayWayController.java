package com.mdd.admin.controller.setting;


import com.alibaba.fastjson2.JSONArray;
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
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/adminapi/setting.pay.pay_way")
@Api(tags = "配置支付参数")
public class SettingPayWayController {

    @Resource
    ISettingPaymentService iSettingPaymentService;

    @GetMapping("/getPayWay")
    @ApiOperation(value="支付方式列表")
    public AjaxResult<HashMap> method() {
        HashMap list = iSettingPaymentService.getPayWay();
        return AjaxResult.success(list);
    }

    @PostMapping("/setPayWay")
    @ApiOperation(value="支付方式编辑")
    public AjaxResult<Object> setPayWay(@Validated @RequestBody HashMap<Integer, List<SettingPaymentMethodVo>> data) {
        iSettingPaymentService.setPayWay(data);
        return AjaxResult.success();
    }

}
