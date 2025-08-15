package com.mdd.front.vo.xmod;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Data;


@Data
@ApiModel("搜索结果")
public class XModSuggestVo implements Serializable {
    @ApiModelProperty(value = "主键")
    private Integer id;
    //游戏是game，mod是mod
    @ApiModelProperty(value = "type")
    private String type;
    
    //游戏是gid，mod是modid
    @ApiModelProperty(value = "uid")
    private String uid;

    @ApiModelProperty(value = "name")
    private String name;

    @ApiModelProperty(value = "image")
    private String image;
}
