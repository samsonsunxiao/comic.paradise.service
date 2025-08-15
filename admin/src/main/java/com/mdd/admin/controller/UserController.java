package com.mdd.admin.controller;

import com.alibaba.fastjson2.JSONObject;
import com.mdd.admin.aop.Log;
import com.mdd.admin.service.IUserService;
import com.mdd.admin.validate.user.UserSearchValidate;
import com.mdd.admin.validate.user.UserUpdateValidate;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.user.UserWalletValidate;
import com.mdd.admin.vo.user.UserVo;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import com.mdd.common.enums.ErrorEnum;
import com.mdd.common.util.StringUtils;
import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping({"adminapi/user/user", "adminapi/user.user"})
@Api(tags = "用户数据管理")
public class UserController {

    @Resource
    IUserService iUserService;

    @GetMapping("/lists")
    @ApiOperation(value="用户列表")
    public AjaxResult<Object> list(@Validated PageValidate pageValidate,
                                               @Validated UserSearchValidate searchValidate) {
        if (StringUtils.isNotNull(searchValidate.getExport()) && searchValidate.getExport().equals(1)) {
            JSONObject ret = iUserService.getExportData(pageValidate, searchValidate);
            return AjaxResult.success(ret);
        } else if (StringUtils.isNotNull(searchValidate.getExport()) && searchValidate.getExport().equals(2)) {
            String path = iUserService.export(searchValidate);
            return AjaxResult.success(2, new JSONObject() {{
                put("url", path);
            }}, ErrorEnum.SHOW_MSG.getCode());
        } else {
            PageResult<UserVo> list = iUserService.list(pageValidate, searchValidate);
            return AjaxResult.success(list);
        }
    }

    @GetMapping("/detail")
    @ApiOperation(value="用户详情")
    public AjaxResult<UserVo> detail(@Validated @IDMust() @RequestParam("id") Integer id) {
        UserVo vo = iUserService.detail(id);
        return AjaxResult.success(vo);
    }

    @Log(title = "用户编辑")
    @PostMapping("/edit")
    @ApiOperation(value="用户编辑")
    public AjaxResult<Object> edit(@Validated @RequestBody UserUpdateValidate updateValidate) {
        iUserService.edit(updateValidate);
        return AjaxResult.success();
    }

    @Log(title = "余额调整")
    @PostMapping("/adjustMoney")
    @ApiOperation(value="余额调整")
    public AjaxResult<Object> adjustWallet(@Validated @RequestBody UserWalletValidate walletValidate) {
        iUserService.adjustWallet(walletValidate);
        return AjaxResult.success();
    }

}
