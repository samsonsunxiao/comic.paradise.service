package com.mdd.admin.vo.game;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


@Data
@ApiModel("Game列表")
public class GameListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "name")
    private String name;
    
    @ApiModelProperty("详情链接")
    private String detailUrl = "";

    @ApiModelProperty(value = "封面")
    private String coverImage = "";
    
    @ApiModelProperty("游戏ID")
    private String gid = "";

    @ApiModelProperty("评分")
    private Float score = 0.0f;

    @ApiModelProperty("mod数")
    private Long modcount = Long.valueOf(0);

    @ApiModelProperty("mod数")
    private Long modOnlineCount = Long.valueOf(0);

    @ApiModelProperty(value = "状态")
    private String status;

}
