package com.mdd.admin.vo.setting;

import com.alibaba.fastjson2.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("登录设置Vo")
public class SettingLoginVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "登录方式")
    private JSONArray loginWay;

    @ApiModelProperty(value = "强制绑定手机")
    private Integer coerceMobile;

    @ApiModelProperty(value = "是否开启协议")
    private Integer loginAgreement;

    @ApiModelProperty(value = "第三方的登录")
    private Integer thirdAuth;

    @ApiModelProperty(value = "微信登录")
    private Integer wechatAuth;

    @ApiModelProperty(value = "qq登录")
    private Integer qqAuth;

}
