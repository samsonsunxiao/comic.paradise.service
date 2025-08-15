package com.mdd.admin.controller.setting;

import com.mdd.admin.aop.Log;
import com.mdd.admin.service.ISettingCopyrightService;
import com.mdd.admin.service.ISettingProtocolService;
import com.mdd.admin.service.ISettingWebsiteService;
import com.mdd.admin.validate.setting.SettingCopyrightValidate;
import com.mdd.admin.validate.setting.SettingAgreementValidate;
import com.mdd.admin.validate.setting.SettingSiteStatisticsValidate;
import com.mdd.admin.validate.setting.SettingWebsiteValidate;
import com.mdd.admin.vo.setting.SettingCopyrightVo;
import com.mdd.admin.vo.setting.SettingAgreementVo;
import com.mdd.admin.vo.setting.SettingSiteStatisticsVo;
import com.mdd.admin.vo.setting.SettingWebsiteVo;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.util.ConfigUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("adminapi/setting.web.web_setting")
@Api(tags = "配置网站信息")
public class SettingWebsiteController {

    @Resource
    ISettingWebsiteService iSettingWebsiteService;

    @Resource
    ISettingCopyrightService iSettingCopyrightService;

    @Resource
    ISettingProtocolService iSettingProtocolService;

    @GetMapping("/getWebsite")
    @ApiOperation(value="网站配置信息")
    public AjaxResult<SettingWebsiteVo> getWebsite() {
        SettingWebsiteVo detail = iSettingWebsiteService.getWebsite();
        return AjaxResult.success(detail);
    }

    @Log(title = "网站配置编辑")
    @PostMapping("/setWebsite")
    @ApiOperation(value="网站配置编辑")
    public AjaxResult<Object> setWebsite(@Validated @RequestBody SettingWebsiteValidate websiteValidate) {
        iSettingWebsiteService.setWebsite(websiteValidate);
        return AjaxResult.success();
    }



    @GetMapping("/getCopyright")
    @ApiOperation(value="网站版权信息")
    public AjaxResult<List<SettingCopyrightVo>> getCopyright() {
        List<SettingCopyrightVo> list = iSettingCopyrightService.getCopyright();
        return AjaxResult.success(list);
    }

    @Log(title = "网站版权编辑")
    @PostMapping("/setCopyright")
    @ApiOperation(value="网站版权编辑")
    public AjaxResult<Object> setCopyright(@Validated @RequestBody SettingCopyrightValidate copyrightValidate) {
        iSettingCopyrightService.setCopyright(copyrightValidate);
        return AjaxResult.success();
    }

    @GetMapping("/getAgreement")
    @ApiOperation(value="政策协议信息")
    public AjaxResult<SettingAgreementVo> getAgreement() {
        SettingAgreementVo detail = iSettingProtocolService.getAgreement();
        return AjaxResult.success(detail);
    }

    @Log(title = "政策协议编辑")
    @PostMapping("/setAgreement")
    @ApiOperation(value="政策协议编辑")
    public AjaxResult<Object> setAgreement(@Validated @RequestBody SettingAgreementValidate protocolValidate) {
        iSettingProtocolService.setAgreement(protocolValidate);
        return AjaxResult.success();
    }


    @GetMapping("/getSiteStatistics")
    @ApiOperation(value="获取站点统计配置")
    public AjaxResult<SettingSiteStatisticsVo> getSiteStatistics() {
        SettingSiteStatisticsVo vo = new SettingSiteStatisticsVo();
        vo.setClarityCode(ConfigUtils.get("siteStatistics", "clarity_code", ""));
        return AjaxResult.success(vo);
    }

    @Log(title = "站点统计配置")
    @PostMapping("/setSiteStatistics")
    @ApiOperation(value="站点统计配置")
    public AjaxResult<Object> setSiteStatistics(@Validated @RequestBody SettingSiteStatisticsValidate settingSiteStatisticsValidate) {
        ConfigUtils.set("siteStatistics", "clarity_code", settingSiteStatisticsValidate.getClarityCode());
        return AjaxResult.success();
    }

}
