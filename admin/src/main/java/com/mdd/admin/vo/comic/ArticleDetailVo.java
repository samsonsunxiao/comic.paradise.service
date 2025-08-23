package com.mdd.admin.vo.comic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

import com.mdd.common.entity.comic.Chapter;

@Data
@ApiModel("Article")
public class ArticleDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "分类ID")
    private String categoryId;

    @ApiModelProperty(value = "名称")
    private String title;

    @ApiModelProperty(value = "描述")
    private String descript = "";

    @ApiModelProperty(value = "作者")
    private String author;

    @ApiModelProperty(value = "封面")
    private String coverImage;

    @ApiModelProperty(value = "状态")
    private Integer state;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "评分")
    private Float score;

    @ApiModelProperty(value = "访问")
    private Integer views;

    @ApiModelProperty(value = "漫画状态，offline-下架，online-上架")
    private String status;

    @ApiModelProperty(value = "是否需要VIP下载")
    private Boolean vip;

    @ApiModelProperty(value = "章节列表")
    private List<Chapter>  chapters;
}