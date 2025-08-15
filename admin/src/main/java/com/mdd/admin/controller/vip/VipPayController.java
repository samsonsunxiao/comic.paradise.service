package com.mdd.admin.controller.vip;

import com.mdd.common.entity.vip.PayModel;
import com.mdd.common.validator.annotation.IDMust;
import com.mdd.admin.aop.Log;
import com.mdd.admin.service.vip.IVipPayService;
import com.mdd.admin.validate.commons.IdValidate;
import com.mdd.admin.validate.vip.PaySaveValidate;
import com.mdd.common.aop.NotLogin;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import javax.annotation.Resource;

@RestController
@RequestMapping("adminapi/vip/pay")
@Api(tags = "支付模式管理")
public class VipPayController {

    @Resource
    @Autowired
    IVipPayService iVipPayService;

    @NotLogin
    @GetMapping("/all")
    @ApiOperation(value="所有支付模式列表")
    public AjaxResult<List<PayModel>> all() {
        List<PayModel> listPay = iVipPayService.all();
        return AjaxResult.success(listPay);
    }
    
    @NotLogin
    @GetMapping("/list")
    @ApiOperation(value="支付模式列表")
    public AjaxResult<PageResult<PayModel>> list() {
        PageResult<PayModel> listPay = iVipPayService.list();
        return AjaxResult.success(listPay);
    }
    
    @NotLogin
    @GetMapping("/detail")
    @ApiOperation(value="支付模式详情")
    public AjaxResult<PayModel> detail(@Validated @IDMust() @RequestParam("key") String key) {
        PayModel detail = iVipPayService.detail(key);
        return AjaxResult.success(detail);
    }

    @NotLogin
    @PostMapping("/save")
    @ApiOperation(value="保存")
    public AjaxResult<Object> save(@Validated @RequestBody PaySaveValidate saveValidate) {
        iVipPayService.save(saveValidate);
        return AjaxResult.success();
    }

    @NotLogin
    @Log(title = "删除")
    @PostMapping("/del")
    @ApiOperation(value="删除")
    public AjaxResult<Object> del(@Validated @RequestBody IdValidate idValidate) {
        iVipPayService.del(idValidate.getId());
        return AjaxResult.success();
    }
}
