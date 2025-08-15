package com.mdd.admin.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("计划任务列表Vo")
public class CrontabListedVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("任务名称")
    private String name;

    @ApiModelProperty("类型 1-定时任务")
    private Integer type;

    private String typeDesc;

    @ApiModelProperty("是否系统任务 0-否 1-是")
    private Integer system;

    @ApiModelProperty("备注信息")
    private String remark;

    @ApiModelProperty("执行命令")
    private String command;

    @ApiModelProperty(" 执行状态: 1=正在运行, 2=任务停止, 3=发生错误")
    private Integer status;

    private String statusDesc;

    @ApiModelProperty(" 参数")
    private String params;

    @ApiModelProperty("执行规则")
    private String expression;

    @ApiModelProperty("错误信息")
    private String error;

    @ApiModelProperty("结束时间")
    private Long lastTime;

    @ApiModelProperty("最大执行时长")
    private Long time;

    @ApiModelProperty("最大执行时长")
    private Long maxTime;

    @ApiModelProperty("创建时间")
    private Long createTime;

    @ApiModelProperty("更新时间")
    private Long updateTime;

    @ApiModelProperty("删除时间")
    private Long deleteTime;

}
