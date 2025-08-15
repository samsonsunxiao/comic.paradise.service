package com.mdd.admin.validate.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("退款记录搜索参数")
public class FinanceRefundValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("充值订单id")
    private Integer recharge_id;

    @NotNull(message = "退款比例不能为空")
    @ApiModelProperty("退款比例")
    private Integer refundRate;
}
