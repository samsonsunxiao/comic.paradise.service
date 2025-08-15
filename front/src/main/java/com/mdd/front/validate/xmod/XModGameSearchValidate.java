package com.mdd.front.validate.xmod;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
@ApiModel("游戏查询条件")
public class XModGameSearchValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "module")
    private String module;

    @ApiModelProperty(value = "查询模块")
    private String param;

}
