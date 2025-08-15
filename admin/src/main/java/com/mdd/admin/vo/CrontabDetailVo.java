package com.mdd.admin.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("计划任务详情Vo")
public class CrontabDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "任务ID")
    private Integer id;

    @ApiModelProperty("任务名称")
    private String name;

    @ApiModelProperty("类型 1-定时任务")
    private Integer type;

    @ApiModelProperty("是否系统任务 0-否 1-是")
    private Integer system;

    @ApiModelProperty("备注信息")
    private String remark;

    @ApiModelProperty("执行命令")
    private String command;

    @ApiModelProperty(" 执行状态: 1=正在运行, 2=任务停止, 3=发生错误")
    private Integer status;

    @ApiModelProperty(" 参数")
    private String params;

    @ApiModelProperty("执行规则")
    private String expression;

}
