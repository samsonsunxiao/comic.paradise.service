package com.mdd.front.vo.article;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "文章详情Vo")
public class ArticleDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "文章ID")
    private Integer id;

    @ApiModelProperty("摘要")
    @JsonProperty(value = "abstract")
    private String abstractField;
    @ApiModelProperty(value = "文章作者")
    private String author;
    @ApiModelProperty(value = "分类ID")
    private Integer cid;
    @ApiModelProperty(value = "浏览数量")
    private Integer click;
    @ApiModelProperty(value = "文章标题")
    private String title;
    @ApiModelProperty(value = "是否收藏")
    private Boolean collect;
    @ApiModelProperty(value = "文章内容")
    private String content;
    @ApiModelProperty(value = "创建时间")
    private String createTime;
    @ApiModelProperty(value = "更新时间")
    private String updateTime;
    @ApiModelProperty("简介")
    private String desc;
    @ApiModelProperty(value = "文章封面")
    private String image;
    @ApiModelProperty("是否显示: [0=否, 1=是]")
    private Integer isShow;
    @ApiModelProperty(value = "排序编号")
    private Integer sort;

}
