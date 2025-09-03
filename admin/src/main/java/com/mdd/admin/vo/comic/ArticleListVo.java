package com.mdd.admin.vo.comic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


@Data
@ApiModel("漫画 列表")
public class ArticleListVo implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "漫画ID")
    private String comicId;

    @ApiModelProperty(value = "分类")
    private String category;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "作者")
    private String author;

    @ApiModelProperty(value = "文章状态，是否完结")
    private Integer state;

    @ApiModelProperty(value = "封面")
    private String coverImage;

    @ApiModelProperty(value = "漫画类型")
    private String type;
    
    @ApiModelProperty(value = "章节数")
    private Long chapterCount = Long.valueOf(0);

    @ApiModelProperty(value = "更新时间")
    private String updatedAt;

    @ApiModelProperty(value = "漫画状态，offline-下架，online-上架")
    private String status;
}