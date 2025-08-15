package com.mdd.common.entity.admin;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("系统管理员实体")
public class Admin implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type=IdType.AUTO)
    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("是否超级管理员 0-否 1-是")
    private Integer root;

    @ApiModelProperty("用户账号")
    private String name;

    @ApiModelProperty("用户头像")
    private String avatar;

    @ApiModelProperty("用户昵称")
    private String account;

    @ApiModelProperty("用户密码")
    private String password;

    @ApiModelProperty("多端登录: [0=否, 1=是]")
    private Integer multipointLogin;

    @ApiModelProperty("是否禁用: [0=否, 1=是]")
    private Integer disable;

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

}
