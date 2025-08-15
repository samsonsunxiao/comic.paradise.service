package com.mdd.admin.controller.vip;

import com.mdd.common.entity.vip.VipLevel;
import com.mdd.common.entity.vip.VipLevelDetailVo;
import com.mdd.common.validator.annotation.IDMust;
import com.mdd.admin.aop.Log;
import com.mdd.admin.service.vip.IVipLevelService;
import com.mdd.admin.validate.commons.IdValidate;
import com.mdd.admin.validate.vip.LevelSaveValidate;
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
@RequestMapping("adminapi/vip/level")
@Api(tags = "等级管理")
public class VipLevelController {

    @Resource
    @Autowired
    IVipLevelService iVipLevelService;
    
    @NotLogin
    @GetMapping("/all")
    @ApiOperation(value="所有等级列表")
    public AjaxResult<List<VipLevel>> all() {
        List<VipLevel> listLevel = iVipLevelService.all();
        return AjaxResult.success(listLevel);
    }
    
    @NotLogin
    @GetMapping("/list")
    @ApiOperation(value="等级列表")
    public AjaxResult<PageResult<VipLevel>> list() {
        PageResult<VipLevel> listLevel = iVipLevelService.list();
        return AjaxResult.success(listLevel);
    }
    
    @NotLogin
    @GetMapping("/detail")
    @ApiOperation(value="等级列表")
    public AjaxResult<VipLevelDetailVo> detail(@Validated @IDMust() @RequestParam("keyid") String keyid) {
        VipLevelDetailVo detail = iVipLevelService.detail(keyid);
        return AjaxResult.success(detail);
    }

    @NotLogin
    @PostMapping("/save")
    @ApiOperation(value="保存")
    public AjaxResult<Object> save(@Validated @RequestBody LevelSaveValidate saveValidate) {
        iVipLevelService.save(saveValidate);
        return AjaxResult.success();
    }

    @NotLogin
    @Log(title = "删除")
    @PostMapping("/del")
    @ApiOperation(value="删除")
    public AjaxResult<Object> del(@Validated @RequestBody IdValidate idValidate) {
        iVipLevelService.del(idValidate.getId());
        return AjaxResult.success();
    }
}
