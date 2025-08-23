package com.mdd.common.entity.comic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;


@Data
@ApiModel("漫画详情")
@TableName(value = "comic_article") 
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "描述")
    private String descript = "";

    @ApiModelProperty(value = "漫画ID")
    private String comicId = "";

    @ApiModelProperty(value = "作者")
    private String author;

    @ApiModelProperty(value = "封面")
    private String coverImage;

    @ApiModelProperty(value = "状态")
    private Integer state;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "创建时间")
    private Long createdAt;

    @ApiModelProperty(value = "更新时间")
    private Long updatedAt;

    @ApiModelProperty(value = "评分")
    private Float score;

    @ApiModelProperty(value = "访问")
    private Integer views;

    @ApiModelProperty(value = "漫画状态，offline-下架，online-上架")
    private String status;

    @ApiModelProperty(value = "是否需要VIP下载")
    private Boolean vip;
}
