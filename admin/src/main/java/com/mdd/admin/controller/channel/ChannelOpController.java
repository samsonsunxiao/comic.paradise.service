package com.mdd.admin.controller.channel;

import com.mdd.admin.service.IChannelOpService;
import com.mdd.admin.validate.channel.ChannelOpValidate;
import com.mdd.admin.vo.channel.ChannelOpVo;
import com.mdd.common.core.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/adminapi/channel.open_setting/")
@Api(tags = "微信开放渠道")
public class ChannelOpController {

    @Resource
    IChannelOpService iChannelOpService;

    @GetMapping("/getConfig")
    @ApiOperation(value="开放平台设置详情")
    public AjaxResult<Object> getConfig() {
        ChannelOpVo vo = iChannelOpService.getConfig();
        return AjaxResult.success(vo);
    }

    @PostMapping("/setConfig")
    @ApiOperation(value="开放平台设置保存")
    public AjaxResult<Object> save(@Validated @RequestBody ChannelOpValidate opValidate) {
        iChannelOpService.setConfig(opValidate);
        return AjaxResult.success();
    }

}
