package com.mdd.front.validate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

import javax.validation.constraints.NotEmpty;

@Data
@ApiModel("发票搜索请求")
public class InvoiceSearchVaildate implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "用户GUID不能为空")
    @ApiModelProperty(value = "用户GUID")
    private String guid;

    @NotEmpty(message = "订单ID号不能为空")
    @ApiModelProperty(value = "订单ID号")
    private String orderId;

}
