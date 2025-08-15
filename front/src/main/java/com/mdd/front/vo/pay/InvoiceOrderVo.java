package com.mdd.front.vo.pay;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

import com.alibaba.fastjson2.JSONObject;


@Data
@ApiModel("发票订单Vo")
public class InvoiceOrderVo implements Serializable {

    @ApiModelProperty(value = "开票信息")
    private JSONObject invoice;

    @ApiModelProperty(value = "发票信息")
    private JSONObject result;
}
