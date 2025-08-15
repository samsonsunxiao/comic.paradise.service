package com.mdd.front.vo.xmod;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableName;

@Data
@ApiModel("游戏详情")
@TableName(value = "xmod_games") 
public class GameDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "name")
    private String name;
    
    @ApiModelProperty(value = "游戏ID")
    private String gid;

    @ApiModelProperty(value = "描述")
    private String descript = "";

    @ApiModelProperty("评分")
    private float score = 0.0f;
    
    @ApiModelProperty("主图")
    private String image;
    
    @ApiModelProperty("mod列表")
    List<ModSummaryVo> mods;

    @ApiModelProperty("推荐游戏列表")
    List<GameSummaryVo> recommands;
}
