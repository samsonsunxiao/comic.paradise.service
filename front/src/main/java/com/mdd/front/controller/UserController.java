package com.mdd.front.controller;

import com.mdd.common.aop.NotLogin;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.exception.OperateException;
import com.mdd.front.FrontThreadLocal;
import com.mdd.front.service.IUserService;
import com.mdd.front.validate.users.*;
import com.mdd.front.vo.user.UserCenterVo;
import com.mdd.front.vo.user.UserInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/user")
@Api(tags = "用户管理")
public class UserController {

    @Resource
    IUserService iUserService;

    @GetMapping("/center")
    @ApiOperation(value="个人中心")
    public AjaxResult<UserCenterVo> center() {
        Integer userId = FrontThreadLocal.getUserId();
        Integer terminal = FrontThreadLocal.getTerminal();

        UserCenterVo vo = iUserService.center(userId, terminal);
        return AjaxResult.success(vo);
    }

    @GetMapping("/info")
    @ApiOperation(value="个人信息")
    public AjaxResult<UserInfoVo> info() {
        Integer userId = FrontThreadLocal.getUserId();

        UserInfoVo vo = iUserService.info(userId);
        return AjaxResult.success(vo);
    }

    @PostMapping("/setInfo")
    @ApiOperation(value="编辑信息")
    public AjaxResult<Object> setInfo(@Validated @RequestBody UserUpdateValidate updateValidate) {
        Integer userId = FrontThreadLocal.getUserId();

        iUserService.setInfo(updateValidate, userId);
        return AjaxResult.success();
    }

    @PostMapping("/changePassword")
    @ApiOperation(value="修改密码")
    public AjaxResult<Object> changePassword(@Validated @RequestBody UserChangePwdValidate passwordValidate) {
        Integer userId = FrontThreadLocal.getUserId();

        if (passwordValidate.getPassword().equals(passwordValidate.getPasswordConfirm()) == false) {
            throw new OperateException("两次输入的密码不一致");
        }
        iUserService.changePwd(passwordValidate.getPassword(), passwordValidate.getOldPassword(), userId);
        return AjaxResult.success();
    }

    @NotLogin
    @PostMapping("/resetPassword")
    @ApiOperation(value="重置密码")
    public AjaxResult<Object> resetPassword(@Validated @RequestBody ResetPasswordValidate passwordValidate) {
        iUserService.resetPassword(passwordValidate);
        return AjaxResult.success();
    }



    @NotLogin
    @PostMapping("/forgotPwd")
    @ApiOperation(value="忘记密码")
    public AjaxResult<Object> forgotPwd(@Validated @RequestBody UserForgetPwdValidate userForgetPwdValidate) {
        String password = userForgetPwdValidate.getPassword();
        String mobile = userForgetPwdValidate.getMobile();
        String code = userForgetPwdValidate.getCode();

        iUserService.forgotPwd(password, mobile, code);
        return AjaxResult.success();
    }

    @PostMapping("/bindMobile")
    @ApiOperation(value="绑定手机")
    public AjaxResult<Object> bindMobile(@Validated @RequestBody UserPhoneBindValidate mobileValidate) {
        Integer userId = FrontThreadLocal.getUserId();

        iUserService.bindMobile(mobileValidate, userId);
        return AjaxResult.success();
    }

    @PostMapping("/getMobileByMnp")
    @ApiOperation(value="微信手机号")
    public AjaxResult<Object> mnpMobile(@Validated @RequestBody UserPhoneMnpValidate mobileValidate) {
        iUserService.mnpMobile(mobileValidate.getCode().trim());
        return AjaxResult.success();
    }

    @PostMapping("/updateUser")
    @ApiOperation(value="更新新用户信息")
    public AjaxResult<Object> updateData(@Validated @RequestBody NewUserUpdateValidate newUserUpdateValidate) {
        Integer userId = FrontThreadLocal.getUserId();
        iUserService.updateNewUserInfo(newUserUpdateValidate, userId);
        return AjaxResult.success();
    }

    @PostMapping("/bindMnp")
    @ApiOperation(value="绑定小程序")
    public AjaxResult<Object> bindMnp(@Validated @RequestBody UserBindWechatValidate BindMnpValidate) {
        Integer userId = FrontThreadLocal.getUserId();

        iUserService.bindMnp(BindMnpValidate, userId);
        return AjaxResult.success();
    }

    @PostMapping("/bindOa")
    @ApiOperation(value="绑定微信公众号")
    public AjaxResult<Object> bindOa(@Validated @RequestBody UserBindWechatValidate BindOaValidate) {
        Integer userId = FrontThreadLocal.getUserId();

        iUserService.bindOa(BindOaValidate, userId);
        return AjaxResult.success();
    }

}
