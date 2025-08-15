package com.mdd.admin.validate.mod;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
@ApiModel("资源包搜索参数")
public class XModSearchValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "mod ID")
    private String id;
    
    @ApiModelProperty(value = "标题,描述,安装描述")
    private String text;

    @ApiModelProperty(value = "所属游戏ID")
    private String gid;

    @ApiModelProperty(value = "所属游戏")
    private String game;
    
    @ApiModelProperty(value = "来源")
    private String platform;
    
    @ApiModelProperty(value = "状态")
    private String status;
}
