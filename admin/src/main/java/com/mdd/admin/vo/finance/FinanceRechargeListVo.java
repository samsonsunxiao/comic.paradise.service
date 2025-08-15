package com.mdd.admin.vo.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("充值记录列表Vo")
public class FinanceRechargeListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("用户昵称")
    private String nickname;

    @ApiModelProperty("用户头像")
    private String avatar;

    @ApiModelProperty("账号")
    private String acount;

    @ApiModelProperty("订单编号")
    private String sn;

    @ApiModelProperty("支付方式: [2=微信支付, 3=支付宝支付]")
    private String payWay;

    @ApiModelProperty("支付状态: [0=待支付, 1=已支付]")
    private Integer payStatus;

    @ApiModelProperty("退款状态: [0=未退款 , 1=已退款]")
    private Integer refundStatus;

    @ApiModelProperty("支付状态: [0=待支付, 1=已支付]")
    private String payStatusText;

    @ApiModelProperty("支付方式")
    private String payWayText;

    @ApiModelProperty("支付金额")
    private BigDecimal orderAmount;

    @ApiModelProperty("支付时间")
    private String payTime;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("软件渠道号")
    private String channel;

}
