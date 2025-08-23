package com.mdd.admin.vo.comic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("Category")
public class CategoryDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "分类ID")
    private String categoryId;

    @ApiModelProperty(value = "名称")
    private String title;

    @ApiModelProperty(value = "漫画列表")
    private List<SummaryVo>  comics;
}