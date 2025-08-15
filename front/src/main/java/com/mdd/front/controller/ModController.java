package com.mdd.front.controller;

import com.mdd.common.aop.NotLogin;
import com.mdd.common.aop.NotPower;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.mod.XMod;
import com.mdd.common.validator.annotation.IDMust;
import com.mdd.front.FrontThreadLocal;
import com.mdd.front.service.IModService;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.validate.xmod.XModDownloadValidate;
import com.mdd.front.vo.xmod.ModSummaryVo;
import com.mdd.front.vo.xmod.ModDetailVo;
import com.mdd.front.vo.xmod.ModDownloadVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import javax.annotation.Resource;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/mod")
@Api(tags = "MOD api")
public class ModController {
    @Resource
    IModService iModService;

    @NotLogin
    @PostMapping("/detail")
    @ApiOperation(value="MOD详情")
    public AjaxResult<ModDetailVo> detail(@Validated @IDMust() @RequestParam("modid") String modid) {
        ModDetailVo detail = iModService.detail(modid);
        return AjaxResult.success(detail);
    }

    @NotLogin
    @PostMapping("/store")
    @ApiOperation(value="MOD查询")
    public AjaxResult<PageResult<ModSummaryVo>> store(@Validated PageValidate pageValidate, @RequestParam("param") String searchParam) {
        PageResult<ModSummaryVo> mapResond = iModService.listStore(pageValidate, searchParam);
        return AjaxResult.success(mapResond);
    }

    @NotLogin
    @PostMapping("/download")
    @ApiOperation(value="MOD下载查询")
    public AjaxResult<ModDownloadVo> download(@Validated XModDownloadValidate downloadValidate) {
        Integer userId = FrontThreadLocal.getUserId();
        Integer terminal = FrontThreadLocal.getTerminal();
        ModDownloadVo download = iModService.queryDownload(downloadValidate.getModid(),downloadValidate.getSupply(),downloadValidate.getVersion(),terminal,userId);
        return AjaxResult.success(download);
    }
}
