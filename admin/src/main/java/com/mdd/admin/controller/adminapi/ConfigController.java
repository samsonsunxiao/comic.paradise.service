package com.mdd.admin.controller.adminapi;

import com.mdd.admin.service.IIndexService;
import com.mdd.common.aop.NotLogin;
import com.mdd.common.core.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("adminapi/config")
@Api(tags = "主页数据管理")
public class ConfigController {

    @Resource
    IIndexService iIndexService;
    @NotLogin
    @GetMapping("/getConfig")
    @ApiOperation(value="公共配置")
    public AjaxResult<Map<String, Object>> getConfig() {
        Map<String, Object> map = iIndexService.config();
        return AjaxResult.success(map);
    }

}
