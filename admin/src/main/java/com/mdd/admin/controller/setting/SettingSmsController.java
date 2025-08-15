package com.mdd.admin.controller.setting;

import com.alibaba.fastjson2.JSONObject;
import com.mdd.admin.aop.Log;
import com.mdd.admin.service.ISettingSmsService;
import com.mdd.common.core.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 短信设置管理
 */
@RestController
@RequestMapping("/adminapi/notice.sms_config")
@Api(tags = "配置短信引擎")
public class SettingSmsController {

    @Resource
    ISettingSmsService iSettingSmsService;

    @GetMapping("/getConfig")
    @ApiOperation(value="短信引擎列表")
    public AjaxResult<List<Map<String, Object>>> getConfig() {
        List<Map<String, Object>> list = iSettingSmsService.getConfig();
        return AjaxResult.success(list);
    }

    @GetMapping("/detail")
    @ApiOperation(value="短信引擎详情")
    public AjaxResult<Map<String, Object>> detail(String type) {
        Map<String, Object> map = iSettingSmsService.detail(type);
        return AjaxResult.success(map);
    }

    @Log(title = "短信引擎编辑")
    @PostMapping("/setConfig")
    @ApiOperation(value="短信引擎编辑")
    public AjaxResult<Object> setConfig(@RequestBody JSONObject params) {
        iSettingSmsService.setConfig(params);
        return AjaxResult.success("操作成功");
    }

}
