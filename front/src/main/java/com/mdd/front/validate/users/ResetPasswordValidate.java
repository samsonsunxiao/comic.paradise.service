package com.mdd.front.validate.users;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
@ApiModel("修改密码参数")
public class ResetPasswordValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "password参数缺失")
    @Pattern(message = "密码必须是6-20字母+数字组合!", regexp="^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,20}$")
    @ApiModelProperty(value = "新密码", required = true)
    private String password;

    @NotNull(message = "请确认密码")
    @Pattern(message = "确认密码密码必须是6-20字母+数字组合!", regexp="^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,20}$")
    @ApiModelProperty(value = "确认密码", required = true)
    private String passwordConfirm;

    @NotNull(message = "验证码不能为空")
    @ApiModelProperty(value = "code", required = true)
    private String code;

    @NotNull(message = "手机号不能为空")
    @ApiModelProperty(value = "mobile", required = true)
    private String mobile;
}
