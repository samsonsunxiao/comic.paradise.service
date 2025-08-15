package com.mdd.admin.vo.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("管理员列表Vo")
public class SystemAuthAdminListedVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "账号")
    private String account;

    @ApiModelProperty(value = "昵称")
    private String name;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "部门")
    private String deptName;

    @ApiModelProperty(value = "角色")
    private String roleName;

    @ApiModelProperty(value = "岗位")
    private String jobsName;

    private List<Integer> roleId;

    private List<Integer> deptId;

    private List<Integer> jobsId;

    @ApiModelProperty(value = "多端登录: [0=否, 1=是]")
    private Integer multipointLogin;

    @ApiModelProperty(value = "是否禁用: [0=否, 1=是]")
    private Integer disable;

    private String disableDesc;

    private Integer root;

    @ApiModelProperty(value = "最后登录IP")
    private String loginIp;

    @ApiModelProperty(value = "最后登录时间")
    private String loginTime;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "更新时间")
    private String updateTime;

}
