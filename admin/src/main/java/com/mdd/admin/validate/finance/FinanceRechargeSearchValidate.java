package com.mdd.admin.validate.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("充值订单搜索参数")
public class FinanceRechargeSearchValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单编号")
    private String sn;

    @ApiModelProperty(value = "关键词")
    private String user_info;

    @ApiModelProperty(value = "支付方式")
    private Integer pay_way;

    @ApiModelProperty(value = "支付状态")
    private Integer pay_status;

    @ApiModelProperty(value = "开始时间")
    private String start_time;

    @ApiModelProperty(value = "结束时间")
    private String end_time;

    @ApiModelProperty(value = "导出信息")
    private Integer export;
    @ApiModelProperty(value = "file_name")
    private String file_name;

    @ApiModelProperty(value = "page_start")
    private Integer page_start;

    @ApiModelProperty(value = "page_end")
    private Integer page_end;

    @ApiModelProperty(value = "page_size")
    private Integer page_size;

    @ApiModelProperty(value = "page_type")
    private Integer page_type;

    @ApiModelProperty(value = "channel")
    private String channel;
}
