package com.mdd.admin.validate.setting;

import com.alibaba.fastjson2.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("登录信息设置参数")
public class SettingLoginValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "登录方式")
    private JSONArray loginWay;

    @ApiModelProperty(value = "强制绑定手机")
    private Integer coerceMobile = 0;

    @ApiModelProperty(value = "政策协议")
    private Integer loginAgreement = 0;

    @ApiModelProperty(value = "第三方登录")
    private Integer thirdAuth = 0;

    @ApiModelProperty(value = "微信登录")
    private Integer wechatAuth;

    @ApiModelProperty(value = "qq登录")
    private Integer qqAuth;

}
