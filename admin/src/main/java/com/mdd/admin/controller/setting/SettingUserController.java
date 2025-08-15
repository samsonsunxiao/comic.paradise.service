package com.mdd.admin.controller.setting;

import com.mdd.admin.aop.Log;
import com.mdd.admin.service.ISettingUserService;
import com.mdd.admin.validate.setting.SettingUserValidate;
import com.mdd.admin.vo.setting.SettingUserVo;
import com.mdd.common.core.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/adminapi/setting.user.user")
@Api(tags = "配置用户参数")
public class SettingUserController {

    @Resource
    ISettingUserService iSettingUserService;

    @GetMapping("/getConfig")
    @ApiOperation(value="用户设置详情")
    public AjaxResult<SettingUserVo> detail() {
        SettingUserVo vo = iSettingUserService.getConfig();
        return AjaxResult.success(vo);
    }

    @Log(title = "用户设置编辑")
    @PostMapping("/setConfig")
    @ApiOperation(value="用户设置编辑")
    public AjaxResult<Object> setConfig(@Validated @RequestBody SettingUserValidate userValidate) {
        iSettingUserService.setConfig(userValidate);
        return AjaxResult.success();
    }

}
