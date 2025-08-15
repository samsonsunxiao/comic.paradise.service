package com.mdd.admin.vo.decorate;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("装修文章数据Vo")
public class DecorateDataArticleVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "分类")
    private String cateName;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "图片")
    private String image;

    @ApiModelProperty(value = "作者")
    private String author;

    @ApiModelProperty("简介")
    @TableField(value = "`desc`")
    private String desc;

    @ApiModelProperty("摘要")
    @JsonProperty("abstract")
    private String abstractField;

    @ApiModelProperty(value = "访问")
    private Integer click;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty("虚拟浏览量")
    private Integer clickVirtual;

    @ApiModelProperty("实际浏览量")
    private Integer clickActual;

    @ApiModelProperty(value = "是否显示")
    private Integer isShow;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

}
