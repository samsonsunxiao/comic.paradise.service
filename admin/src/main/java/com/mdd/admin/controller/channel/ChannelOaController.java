package com.mdd.admin.controller.channel;

import com.mdd.admin.aop.Log;
import com.mdd.admin.service.IChannelOaConfigService;
import com.mdd.admin.validate.channel.ChannelOaValidate;
import com.mdd.admin.vo.channel.ChannelOaVo;
import com.mdd.common.core.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/adminapi/channel.official_account_setting")
@Api(tags = "公众号渠道设置")
public class ChannelOaController {

    @Resource
    IChannelOaConfigService iChannelOaConfigService;

    @GetMapping("/getConfig")
    @ApiOperation(value="公众号渠道设置详情")
    public AjaxResult<ChannelOaVo> getConfig() {
        ChannelOaVo vo = iChannelOaConfigService.getConfig();
        return AjaxResult.success(vo);
    }

    @Log(title = "公众号渠道设置保存")
    @PostMapping("/setConfig")
    @ApiOperation(value="公众号渠道设置保存")
    public AjaxResult<Object> setConfig(@Validated @RequestBody ChannelOaValidate channelOaValidate) {
        iChannelOaConfigService.setConfig(channelOaValidate);
        return AjaxResult.success();
    }

}
