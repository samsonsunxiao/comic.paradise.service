package com.mdd.front.validate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("OCPC请求")
public class OcpcValidate implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "请输入OCPC值")
    @ApiModelProperty(value = "OCPC值")
    private String id;

    @ApiModelProperty(value = "渠道")
    private String channel;

    @ApiModelProperty(value = "产品ID")
    private String pid;
}
