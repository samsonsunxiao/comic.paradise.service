package com.mdd.common.entity.module;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

@Data
@ApiModel("MODULE和游戏关系表")
public class XModuleGameVo implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value = "主键")
    private Integer id;
    
    @ApiModelProperty(value = "mid")
    private Integer mid;
    
    @ApiModelProperty(value = "moduleid")
    private String moduleid;
    
    @ApiModelProperty(value = "modulename")
    private String modulename;
    
    @ApiModelProperty(value = "gid")
    private String gid;

    @ApiModelProperty(value = "game")
    private String game;

    @ApiModelProperty(value = "image")
    private String image;

    @ApiModelProperty(value = "score")
    private Float score;
}
