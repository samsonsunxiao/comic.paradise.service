package com.mdd.admin.validate.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("系统岗位搜索参数")
public class JobsSearchValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编码")
    private String code;

    @ApiModelProperty(value = "岗位名称")
    private String name;

    @ApiModelProperty(value = "是否停用")
    private Integer status;

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
