package com.mdd.common.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("用户信息实体")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("编号")
    private Integer sn;

    @ApiModelProperty("用户头像")
    private String avatar;

    @ApiModelProperty("真实姓名")
    private String realName;

    @ApiModelProperty("用户昵称")
    private String nickname;

    @ApiModelProperty("用户账号")
    private String account;

    @ApiModelProperty("用户密码")
    private String password;

    @ApiModelProperty("用户电话")
    private String mobile;

    @ApiModelProperty("用户性别: [1=男, 2=女]")
    private Integer sex;

    @ApiModelProperty("注册渠道")
    private Integer channel;

    @ApiModelProperty("是否禁用: [0=否, 1=是]")
    private Integer isDisable;

    @ApiModelProperty("用户钱包")
    private BigDecimal userMoney;

    @ApiModelProperty("累计充值")
    private BigDecimal totalRechargeAmount;

//    @ApiModelProperty("加密盐巴")
//    private String salt;
    @ApiModelProperty("最后登录IP")
    private String loginIp;

    @ApiModelProperty("最后登录时间")
    private Long loginTime;

    @ApiModelProperty("创建时间")
    private Long createTime;

    @ApiModelProperty("更新时间")
    private Long updateTime;

    @ApiModelProperty("删除时间")
    private Long deleteTime;

    @ApiModelProperty("是否为新用户: [0=否, 1=是]")
    private Integer isNewUser;

}
