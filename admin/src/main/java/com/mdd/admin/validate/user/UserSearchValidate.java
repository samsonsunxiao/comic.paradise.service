package com.mdd.admin.validate.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("用户搜索参数")
public class UserSearchValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "渠道")
    private Integer channel;

    @ApiModelProperty(value = "关键词")
    private String keyword;

    @ApiModelProperty(value = "开始时间")
    private String create_time_start;

    @ApiModelProperty(value = "结束时间")
    private String create_time_end;

    @ApiModelProperty(value = "导出信息")
    private Integer export;
    @ApiModelProperty(value = "file_name")
    private String file_name;

    @ApiModelProperty(value = "page_start")
    private Integer page_start;

    @ApiModelProperty(value = "page_end")
    private Integer page_end;

    @ApiModelProperty(value = "page_size")
    private Integer page_size;

    @ApiModelProperty(value = "page_type")
    private Integer page_type;

}
