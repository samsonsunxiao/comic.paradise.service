package com.mdd.admin.vo.comic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


@Data
@ApiModel("Category 列表")
public class CategoryListVo implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "分类ID")
    private String categoryId;

    @ApiModelProperty(value = "标题")
    private String title;
    
    @ApiModelProperty(value = "漫画数")
    private Long count = Long.valueOf(0);
}