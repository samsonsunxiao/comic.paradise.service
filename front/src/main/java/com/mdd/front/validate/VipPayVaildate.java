package com.mdd.front.validate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("会员支付请求")
public class VipPayVaildate implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "请确认等级")
    @ApiModelProperty(value = "等级值")
    private Integer vip;

    @NotNull(message = "请确认支付模式")
    @ApiModelProperty(value = "支付模式")
    private String pay;

    @ApiModelProperty(value = "软件渠道")
    private String supply;

    @ApiModelProperty(value = "软件版本")
    private String version;

    @ApiModelProperty(value = "文件名")
    private String file;
}
