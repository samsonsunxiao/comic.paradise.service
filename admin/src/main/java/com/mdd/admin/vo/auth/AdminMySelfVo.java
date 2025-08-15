package com.mdd.admin.vo.auth;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("adminVo")
public class AdminMySelfVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "当前管理员角色拥有的菜单")
    private JSONArray menu;

    @ApiModelProperty(value = "当前管理员橘色拥有的按钮权限")
    private List<String> permissions;

    @ApiModelProperty(value = "user")
    private AuthMySelfVo user;
}
