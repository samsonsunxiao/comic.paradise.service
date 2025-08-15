package com.mdd.front.vo.pay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "支付状态Vo")
public class  AppPayStatusVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "支付状态: [0=待支付, 1=已支付]")
    private Integer payStatus;

    @ApiModelProperty(value = "支付成功的SN码")
    private String orderSn;

    @ApiModelProperty(value = "支付哪种类型")
    private Integer payType;

    @ApiModelProperty(value = "支付金额")
    private BigDecimal amount;
}
