package com.mdd.admin.vo.auth;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("管理员")
public class AuthMySelfVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    @ApiModelProperty(value = "账号")
    private String account;

    @ApiModelProperty(value = "用户头像")
    private String avatar;
    @ApiModelProperty(value = "部门关联ID")
    @JsonProperty(value = "dept_id")
    private List<Integer> deptId;
    @ApiModelProperty(value = "当前管理员角色拥有的菜单")
    private Integer disable;
    @ApiModelProperty(value = "当前管理员角色拥有的菜单")
    @JsonProperty(value = "jobs_id")
    private List<Integer> jobsId;

    @ApiModelProperty(value = "当前管理员角色拥有的菜单")
    @JsonProperty(value = "multipoint_login")
    private Integer multipointLogin;
    @ApiModelProperty(value = "当前管理员角色拥有的菜单")
    private String name;

    @ApiModelProperty(value = "当前管理员角色拥有的菜单")
    @JsonProperty(value = "role_id")
    private List<Integer> roleId;

    @ApiModelProperty(value = "当前管理员角色拥有的菜单")
    private Integer root;
}
