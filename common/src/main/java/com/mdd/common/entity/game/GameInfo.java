package com.mdd.common.entity.game;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;

@Data
@ApiModel("游戏详情")
@TableName(value = "xmod_games") 
public class GameInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "name")
    private String name;
    
    @ApiModelProperty(value = "游戏ID")
    private String gid;

    @ApiModelProperty(value = "封面")
    private String coverImage = "";
    
    @ApiModelProperty("详情链接")
    private String detailUrl = "";

    @ApiModelProperty(value = "描述")
    private String descript = "";

    @ApiModelProperty("评分")
    private float score = 0.0f;

    @ApiModelProperty(value = "状态")
    private String status;
    
    @ApiModelProperty(value = "上架时间")
    private Long onlineTime;

    @ApiModelProperty(value = "更新时间")
    private Long updateTime;

    @ApiModelProperty(value = "banner广告语")
    private String banner;
}
