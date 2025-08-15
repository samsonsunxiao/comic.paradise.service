package com.mdd.admin.validate.game;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
@ApiModel("游戏搜索参数")
public class GameSearchValidate implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value = "标题,描述,安装描述")
    private String text;

    @ApiModelProperty(value = "游戏名称")
    private String name;
    
    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "最小score")
    private float scoreMin;

    @ApiModelProperty(value = "最大score")
    private float scoreMax;

}
