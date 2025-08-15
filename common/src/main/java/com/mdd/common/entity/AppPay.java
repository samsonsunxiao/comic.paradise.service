package com.mdd.common.entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@ApiModel("软件支付关系表")
@TableName(value = "xmod_app_pay") 
public class AppPay implements Serializable {

    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "软件ID")
    private String pid;

    @ApiModelProperty(value = "用户机器GUID")
    private String guid;

    @ApiModelProperty(value = "订单SN")
    private String orderSn;

    @ApiModelProperty(value = "支付哪种类型")
    private Integer payType;

    @ApiModelProperty(value = "模型类型")
    private String llm;
    
    @ApiModelProperty(value = "请求类型,如: 360, lenovo,空表示自己的请求")
    private String type;
}
