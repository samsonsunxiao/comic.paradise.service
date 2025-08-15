package com.mdd.admin.controller.mod;

import com.mdd.admin.service.IModService;
import com.mdd.admin.vo.mod.XModDetailVo;
import com.mdd.admin.vo.mod.XModListedVo;
import com.mdd.admin.vo.mod.XModOfflineVo;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.mod.XModSaveValidate;
import com.mdd.admin.validate.mod.XModSearchValidate;
import com.mdd.common.aop.NotLogin;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("adminapi/mod")
@Api(tags = "mod资源管理")
public class ModController {

    @Resource
    @Autowired
    IModService iModService;

    @NotLogin
    @GetMapping("/list")
    @ApiOperation(value="资源列表")
    public AjaxResult<PageResult<XModListedVo>> list(@Validated PageValidate pageValidate,
                                                     @Validated XModSearchValidate searchValidate) {
        PageResult<XModListedVo> list = iModService.list(pageValidate, searchValidate);
        return AjaxResult.success(list);
    }

    @NotLogin
    @GetMapping("/detail")
    @ApiOperation(value="详情")
    public AjaxResult<XModDetailVo> detail(@Validated @IDMust() @RequestParam("modid") String modid) {
        XModDetailVo detail = iModService.detail(modid);
        return AjaxResult.success(detail);
    }

    @NotLogin
    @PostMapping("/save")
    @ApiOperation(value="保存")
    public AjaxResult<Object> save(@Validated @RequestBody XModSaveValidate saveValidate) {
        iModService.save(saveValidate);
        return AjaxResult.success();
    }

    @NotLogin
    @GetMapping("/getOneOffline")
    @ApiOperation(value="获取一个还未上线的MOD包")
    public AjaxResult<XModOfflineVo> getOneOffline() {
        XModOfflineVo detail = iModService.getOneOffline();
        return AjaxResult.success(detail);
    }

    @NotLogin
    @GetMapping("/removetemp")
    @ApiOperation(value="删除临时的MOD")
    public AjaxResult<Object> removeTemp(@Validated @IDMust() @RequestParam("modid") String modid) {
        iModService.removeTemp(modid);
        return AjaxResult.success();
    }
}
