package com.mdd.admin.vo.module;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


@Data
@ApiModel("Module 列表")
public class XModuleListVo implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "modid")
    private String moduleid;

    @ApiModelProperty(value = "标题")
    private String name;
    
    @ApiModelProperty(value = "上架游戏数")
    private Long gameCount = Long.valueOf(0);
}