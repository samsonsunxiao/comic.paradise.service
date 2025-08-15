package com.mdd.front.validate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
@ApiModel("发票请求")
public class InvoiceVaildate implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "请输入产品ID")
    //@Pattern(regexp = "xxx|aaa", message = "产品ID必须为 'xxx' 或 'aaa'")
    @Pattern(regexp = "deepseek", message = "产品ID必须为deepseek")
    @ApiModelProperty(value = "产品ID")
    private String pid;

    @ApiModelProperty(value = "软件渠道")
    private String supply;

    @ApiModelProperty(value = "软件版本")
    private String version;

    @NotEmpty(message = "请输入用户GUID")
    @ApiModelProperty(value = "用户GUID")
    private String guid;

    @ApiModelProperty(value = "请求类型，360，lenovo,空表示自己的请求")
    private String type;

    @ApiModelProperty(value = "发票数据")
    private String data;


}
