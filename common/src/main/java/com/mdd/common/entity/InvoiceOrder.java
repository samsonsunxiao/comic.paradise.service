package com.mdd.common.entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@ApiModel("发票订单表")
@TableName(value = "xmod_invoice_order") 
public class InvoiceOrder implements Serializable {

    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "软件ID")
    private String pid;

    @ApiModelProperty(value = "软件渠道")
    private String supply;

    @ApiModelProperty(value = "软件版本")
    private String version;

    @ApiModelProperty(value = "用户机器GUID")
    private String guid;

    @ApiModelProperty(value = "订单ID")
    private String orderId;

    @ApiModelProperty(value = "开票信息")
    private String data;

    @ApiModelProperty(value = "请求类型,如: 360, lenovo,空表示自己的请求")
    private String type;

    @ApiModelProperty(value = "发票信息")
    private String invoice;

    @ApiModelProperty(value = "状态")
    private Integer status;
}
