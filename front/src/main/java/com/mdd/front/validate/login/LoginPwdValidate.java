package com.mdd.front.validate.login;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("账号登录参数")
public class LoginPwdValidate {

    @NotNull(message = "username参数缺失")
    @NotEmpty(message = "账号不能为空")
    @ApiModelProperty(value = "登录账号", required = true)
    private String account;

    @ApiModelProperty(value = "登录密码")
    private String password;

    @ApiModelProperty(value = "验证码登录")
    private String code;

    @ApiModelProperty(value = "终端")
    private Integer scene;


    @ApiModelProperty(value = "终端")
    private Integer terminal;
}
