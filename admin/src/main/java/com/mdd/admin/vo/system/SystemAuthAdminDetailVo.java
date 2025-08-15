package com.mdd.admin.vo.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("管理员详情Vo")
public class SystemAuthAdminDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "是否超级管理员 0-否 1-是")
    private Integer root;

    @ApiModelProperty(value = "角色ID")
    private List<Integer> roleId;

    @ApiModelProperty(value = "部门ID")
    private List<Integer> deptId;

    @ApiModelProperty(value = "岗位ID")
    private List<Integer> jobsId;

    @ApiModelProperty(value = "账号")
    private String account;

    @ApiModelProperty(value = "昵称")
    private String name;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "是否禁用: [0=否, 1=是]")
    private Integer disable;

    @ApiModelProperty(value = "最后登录IP")
    private String loginIp;

    @ApiModelProperty(value = "最后登录时间")
    private String loginTime;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "更新时间")
    private String updateTime;

    @ApiModelProperty(value = "是否支持多处登录：1-是；0-否；")
    private Integer multipointLogin;

}
