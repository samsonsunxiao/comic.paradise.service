package com.mdd.admin.controller.channel;

import com.mdd.admin.aop.Log;
import com.mdd.admin.service.IChannelH5ConfigService;
import com.mdd.admin.validate.channel.ChannelH5Validate;
import com.mdd.admin.vo.channel.ChannelH5Vo;
import com.mdd.common.core.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/adminapi/channel.web_page_setting")
@Api(tags = "移动渠道设置")
public class ChannelH5Controller {

    @Resource
    IChannelH5ConfigService iChannelH5ConfigService;

    @GetMapping("/getConfig")
    @ApiOperation(value="H5渠道设置详情")
    public AjaxResult<ChannelH5Vo> getConfig() {
        ChannelH5Vo vo = iChannelH5ConfigService.getConfig();
        return AjaxResult.success(vo);
    }

    @Log(title = "H5渠道设置保存")
    @PostMapping("/setConfig")
    @ApiOperation(value="H5渠道设置保存")
    public AjaxResult<Object> setConfig(@Validated @RequestBody ChannelH5Validate channelH5Validate) {
        iChannelH5ConfigService.setConfig(channelH5Validate);
        return AjaxResult.success();
    }

}
