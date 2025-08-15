package com.mdd.admin.controller.module;

import com.mdd.admin.service.IModuleService;
import com.mdd.admin.vo.module.XModuleDetailVo;
import com.mdd.admin.vo.module.XModuleListVo;
import com.mdd.admin.aop.Log;
import com.mdd.common.entity.module.XModule;
import com.mdd.admin.validate.commons.IdValidate;
import com.mdd.admin.validate.commons.PageValidate;

import com.mdd.admin.validate.module.XModuleSaveValidate;
import com.mdd.common.aop.NotLogin;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import javax.annotation.Resource;

@RestController
@RequestMapping("adminapi/module")
@Api(tags = "模块管理")
public class ModuleController {

    @Resource
    @Autowired
    IModuleService iModuleService;
    
    @NotLogin
    @GetMapping("/all")
    @ApiOperation(value="模块列表")
    public AjaxResult<List<XModule>> all() {
        List<XModule> listModule = iModuleService.all();
        return AjaxResult.success(listModule);
    }
    
    @NotLogin
    @GetMapping("/list")
    @ApiOperation(value="资源列表")
    public AjaxResult<PageResult<XModuleListVo>> list(@Validated PageValidate pageValidate) {
        PageResult<XModuleListVo> list = iModuleService.list(pageValidate);
        return AjaxResult.success(list);
    }

    @NotLogin
    @GetMapping("/detail")
    @ApiOperation(value="详情")
    public AjaxResult<XModuleDetailVo> detail(@Validated @IDMust() @RequestParam("moduleid") String moduleid) {
        XModuleDetailVo detail = iModuleService.detail(moduleid);
        return AjaxResult.success(detail);
    }
    
    @NotLogin
    @PostMapping("/save")
    @ApiOperation(value="保存")
    public AjaxResult<Object> save(@Validated @RequestBody XModuleSaveValidate saveValidate) {
        iModuleService.save(saveValidate);
        return AjaxResult.success();
    }
    
    @NotLogin
    @Log(title = "删除")
    @PostMapping("/del")
    @ApiOperation(value="删除")
    public AjaxResult<Object> del(@Validated @RequestBody IdValidate idValidate) {
        iModuleService.del(idValidate.getId());
        return AjaxResult.success();
    }
}
