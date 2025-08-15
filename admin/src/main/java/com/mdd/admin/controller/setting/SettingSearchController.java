package com.mdd.admin.controller.setting;

import com.mdd.admin.aop.Log;
import com.mdd.admin.service.ISettingSearchService;
import com.mdd.admin.validate.setting.SettingSearchValidate;
import com.mdd.admin.vo.setting.SettingSearchDetailVo;
import com.mdd.common.core.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/adminapi/setting.hot_search")
@Api(tags = "配置热门搜索")
public class SettingSearchController {

    @Resource
    ISettingSearchService iSettingSearchService;

    @GetMapping("/getConfig")
    @ApiOperation(value="热门搜索详情")
    public AjaxResult<SettingSearchDetailVo> getConfig() {
        SettingSearchDetailVo vo = iSettingSearchService.getConfig();
        return AjaxResult.success(vo);
    }

    @Log(title = "热门搜索编辑")
    @PostMapping("/setConfig")
    @ApiOperation(value="热门搜索编辑")
    public AjaxResult<Object> setConfig(@Validated @RequestBody SettingSearchValidate searchValidate) {
        iSettingSearchService.setConfig(searchValidate);
        return AjaxResult.success("操作成功");
    }

}
