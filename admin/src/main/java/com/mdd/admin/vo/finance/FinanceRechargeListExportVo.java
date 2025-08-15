package com.mdd.admin.vo.finance;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("导出充值记录列表Vo")
public class FinanceRechargeListExportVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("ID")
    @ExcelProperty("ID")
    @ColumnWidth(50)
    private Integer id;

    @ApiModelProperty("用户昵称")
    @ExcelProperty("用户昵称")
    @ColumnWidth(50)
    private String nickname;

    @ApiModelProperty("用户头像")
    @ExcelProperty("用户头像")
    @ColumnWidth(50)
    private String avatar;

    @ApiModelProperty("账号")
    @ExcelProperty("账号")
    @ColumnWidth(50)
    private String account;

    @ApiModelProperty("订单编号")
    @ExcelProperty("订单编号")
    @ColumnWidth(50)
    private String sn;

    @ApiModelProperty("支付方式: [2=微信支付, 3=支付宝支付]")
    @ExcelProperty("支付方式: [2=微信支付, 3=支付宝支付]")
    @ColumnWidth(50)
    private String payWay;

    @ApiModelProperty("支付状态: [0=待支付, 1=已支付]")
    @ExcelProperty("支付状态: [0=待支付, 1=已支付]")
    @ColumnWidth(50)
    private Integer payStatus;

    @ApiModelProperty("退款状态: [0=未退款 , 1=已退款]")
    @ExcelProperty("退款状态: [0=未退款 , 1=已退款]")
    @ColumnWidth(50)
    private Integer refundStatus;

    @ApiModelProperty("支付状态: [0=待支付, 1=已支付]")
    @ExcelProperty("支付状态: [0=待支付, 1=已支付]")
    @ColumnWidth(50)
    private String payStatusText;

    @ApiModelProperty("支付方式")
    @ExcelProperty("支付方式")
    @ColumnWidth(50)
    private String payWayText;

    @ApiModelProperty("支付金额")
    @ExcelProperty("支付金额")
    @ColumnWidth(50)
    private BigDecimal orderAmount;

    @ApiModelProperty("支付时间")
    @ExcelProperty("支付时间")
    @ColumnWidth(50)
    private String payTime;

    @ApiModelProperty("创建时间")
    @ExcelProperty("创建时间")
    @ColumnWidth(50)
    private String createTime;

}
