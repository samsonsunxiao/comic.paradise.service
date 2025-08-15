package com.mdd.front.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "个人中心Vo")
public class UserCenterVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户ID")
    private Integer id;

    @ApiModelProperty(value = "用户编号")
    private Integer sn;
    @ApiModelProperty("用户头像")
    private String avatar;

    @ApiModelProperty("真实姓名")
    private String realName;

    @ApiModelProperty("用户昵称")
    private String nickname;

    @ApiModelProperty("用户账号")
    private String account;

    @ApiModelProperty("用户电话")
    private String mobile;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("是否为新用户: [0=否, 1=是]")
    private Integer isNewUser;

    @ApiModelProperty("有密码")
    private Boolean hasPassword;

    @ApiModelProperty("用户性别: [1=男, 2=女]")
    private String sex;

    @ApiModelProperty("用户钱包")
    private BigDecimal userMoney;

    @ApiModelProperty("是否绑定微信")
    private Boolean isAuth;


}
