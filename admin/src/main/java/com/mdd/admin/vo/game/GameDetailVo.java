package com.mdd.admin.vo.game;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

import com.mdd.common.entity.mod.XModImages;
import com.mdd.common.entity.mod.XModFiles;
import com.mdd.common.entity.module.XModule;
@Data
@ApiModel("游戏详情Vo")
public class GameDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "标题")
    private String name = "";

    @ApiModelProperty(value = "游戏ID")
    private String gid = "";
    
    @ApiModelProperty(value = "详情链接")
    private String detailUrl = "";

    @ApiModelProperty(value = "游戏图片")
    private List<XModImages>  images;
    
    @ApiModelProperty(value = "游戏前置修改器包")
    private List<XModFiles>  managers;
    
    @ApiModelProperty(value = "描述")
    private String descript = "";
    
    @ApiModelProperty("评分")
    private Float score = 0.0f;

    @ApiModelProperty(value = "封面")
    private String coverImage = "";

    @ApiModelProperty(value = "状态")
    private String status = "";

    @ApiModelProperty(value = "所属模块")
    private List<XModule>  modules;

    @ApiModelProperty(value = "上架时间")
    private String onlineTime = "";

    @ApiModelProperty(value = "banner广告语")
    private String banner;
}
