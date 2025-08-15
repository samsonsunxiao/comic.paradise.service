package com.mdd.front.vo.xmod;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "游戏概略信息")
public class GameSummaryVo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value = "name")
    private String name;

    @ApiModelProperty(value = "gid")
    private String gid;

    @ApiModelProperty(value = "image")
    private String image;
    
    @ApiModelProperty(value = "score")
    private float score;

    @ApiModelProperty(value = "modulename")
    private String modulename;

    @ApiModelProperty(value = "modcount")
    private Long modcount;

    @ApiModelProperty(value = "上架时间")
    private String onlineTime;

    @ApiModelProperty(value = "banner广告语")
    private String banner;
}
