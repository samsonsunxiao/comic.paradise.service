package com.mdd.admin.vo.game;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


@Data
@ApiModel("Game列表")
public class GameNameVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "name")
    private String name;

    @ApiModelProperty("游戏ID")
    private String gid = "";

    @ApiModelProperty("缩略图")
    private String image = "";
}
