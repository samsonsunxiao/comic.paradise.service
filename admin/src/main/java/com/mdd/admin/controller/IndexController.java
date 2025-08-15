package com.mdd.admin.controller;

import com.mdd.admin.service.IIndexService;
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
@RequestMapping("/adminapi/workbench")
@Api(tags = "主页数据管理")
public class IndexController {

    @Resource
    IIndexService iIndexService;

    @GetMapping("/index")
    @ApiOperation(value="控制台")
    public AjaxResult<Map<String, Object>> index() {
        Map<String, Object> map = iIndexService.index();
        return AjaxResult.success(map);
    }

}
