package com.mdd.admin.vo.comic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


@Data
@ApiModel("漫画标题列表")
public class SummaryVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "漫画标题")
    private String title;

    @ApiModelProperty("漫画ID")
    private String comicId = "";

    @ApiModelProperty("缩略图")
    private String coverImage = "";
}
