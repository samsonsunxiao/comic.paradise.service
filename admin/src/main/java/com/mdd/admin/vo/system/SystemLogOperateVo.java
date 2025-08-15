package com.mdd.admin.vo.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("操作日志Vo")
public class SystemLogOperateVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("操作人ID")
    private Integer adminId;

    private String adminName;

    private String account;

    @ApiModelProperty("操作标题")
    private String action;

    @ApiModelProperty("请求类型: GET/POST/PUT")
    private String type;

    @ApiModelProperty("请求接口")
    private String url;

    @ApiModelProperty("请求参数")
    private String params;

    @ApiModelProperty("请求结果")
    private String result;

    @ApiModelProperty("请求IP")
    private String ip;

    @ApiModelProperty("创建时间")
    private String createTime;

}
