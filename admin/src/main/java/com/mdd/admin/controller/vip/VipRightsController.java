package com.mdd.admin.controller.vip;

import com.mdd.common.entity.vip.VipRights;
import com.mdd.common.validator.annotation.IDMust;
import com.mdd.admin.aop.Log;
import com.mdd.admin.service.vip.IVipRightsService;
import com.mdd.admin.validate.commons.IdValidate;
import com.mdd.admin.validate.vip.RightsSaveValidate;
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
@RequestMapping("adminapi/vip/rights")
@Api(tags = "权益管理")
public class VipRightsController {

    @Resource
    @Autowired
    IVipRightsService iVipRightsService;
    
    
    @NotLogin
    @GetMapping("/all")
    @ApiOperation(value="所有权益列表")
    public AjaxResult<List<VipRights>> all() {
        List<VipRights> listRights = iVipRightsService.all();
        return AjaxResult.success(listRights);
    }

    @NotLogin
    @GetMapping("/list")
    @ApiOperation(value="权益列表")
    public AjaxResult<PageResult<VipRights>> list() {
        PageResult<VipRights> listRights = iVipRightsService.list();
        return AjaxResult.success(listRights);
    }
    
    @NotLogin
    @GetMapping("/detail")
    @ApiOperation(value="权益详情")
    public AjaxResult<VipRights> detail(@Validated @IDMust() @RequestParam("key") String key) {
        VipRights detail = iVipRightsService.detail(key);
        return AjaxResult.success(detail);
    }

    @NotLogin
    @PostMapping("/save")
    @ApiOperation(value="保存")
    public AjaxResult<Object> save(@Validated @RequestBody RightsSaveValidate saveValidate) {
        iVipRightsService.save(saveValidate);
        return AjaxResult.success();
    }

    @NotLogin
    @Log(title = "删除")
    @PostMapping("/del")
    @ApiOperation(value="删除")
    public AjaxResult<Object> del(@Validated @RequestBody IdValidate idValidate) {
        iVipRightsService.del(idValidate.getId());
        return AjaxResult.success();
    }
}
