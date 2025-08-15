package com.mdd.admin.controller.channel;

import com.mdd.admin.aop.Log;
import com.mdd.admin.service.IChannelMpConfigService;
import com.mdd.admin.validate.channel.ChannelMpValidate;
import com.mdd.admin.vo.channel.ChannelMpVo;
import com.mdd.common.core.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/adminapi/channel.mnp_settings")
@Api(tags = "微信程序渠道")
public class ChannelMpController {

    @Resource
    IChannelMpConfigService iChannelMpConfigService;

    @GetMapping("/getConfig")
    @ApiOperation(value="微信程序渠道设置详情")
    public AjaxResult<ChannelMpVo> getConfig() {
        ChannelMpVo vo = iChannelMpConfigService.getConfig();
        return AjaxResult.success(vo);
    }

    @Log(title = "微信小程序渠道设置保存")
    @PostMapping("/setConfig")
    @ApiOperation(value="微信程序渠道设置保存")
    public AjaxResult<Object> setConfig(@Validated @RequestBody ChannelMpValidate channelMpValidate) {
        iChannelMpConfigService.setConfig(channelMpValidate);
        return AjaxResult.success();
    }

}
