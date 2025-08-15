package com.mdd.admin.vo.system;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("系统登录Vo")
public class SystemLoginVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "管理员ID")
    private Integer id;

    @ApiModelProperty(value = "登录令牌")
    private String token;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "用户头像")
    private String avatar;
    @ApiModelProperty(value = "用户头像")
    @JsonProperty("role_name")
    private String roleName;
}
