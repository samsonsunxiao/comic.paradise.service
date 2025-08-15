package com.mdd.front.vo.article;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "文章收藏Vo")
public class ArticleCollectVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "收藏主键")
    private Integer id;

    @ApiModelProperty(value = "文章ID")
    private Integer articleId;

    @ApiModelProperty(value = "文章标题")
    private String title;

    @ApiModelProperty(value = "文章封面")
    private String image;

    @ApiModelProperty(value = "文章简介")
    private String desc;

    @ApiModelProperty(value = "是否显示:1-是.0-否")
    private Integer isShow;

    @ApiModelProperty(value = "虚拟浏览量")
    private Integer clickVirtual;

    @ApiModelProperty(value = "实际浏览量")
    private Integer clickActual;

    @ApiModelProperty(value = "创建时间")
    private Object createTime;

    @ApiModelProperty(value = "收集时间")
    private Object collectTime;

//    @ApiModelProperty(value = "创建时间")
//    @JsonProperty("create_time")
//    private String createTimeStr;
//
//    @ApiModelProperty(value = "收集时间")
//    @JsonProperty("collect_time")
//    private String collectTimeStr;
}
