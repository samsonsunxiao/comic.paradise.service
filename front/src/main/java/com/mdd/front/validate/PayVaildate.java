package com.mdd.front.validate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
@ApiModel("支付请求")
public class PayVaildate implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "价格")
    private BigDecimal amount;

    @NotEmpty(message = "请输入产品ID")
    //@Pattern(regexp = "xxx|aaa", message = "产品ID必须为 'xxx' 或 'aaa'")
    @Pattern(regexp = "deepseek", message = "产品ID必须为deepseek")
    @ApiModelProperty(value = "产品ID")
    private String pid;

    @ApiModelProperty(value = "软件渠道")
    private String supply;

    @ApiModelProperty(value = "软件版本")
    private String version;

    @ApiModelProperty(value = "文件名")
    private String file;

    @NotEmpty(message = "请输入用户GUID")
    @ApiModelProperty(value = "用户GUID")
    private String guid;

    @ApiModelProperty(value = "payType")
    private Integer payType;

    @ApiModelProperty(value = "llm")
    private String llm;

    @ApiModelProperty(value = "type")
    private String type;
}
