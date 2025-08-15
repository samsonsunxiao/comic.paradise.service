package com.mdd.admin.validate.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("余额记录搜索参数")
public class FinanceWalletSearchValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "关键词")
    private String user_info;

    @ApiModelProperty(value = "类型")
    private Integer change_type;

    @ApiModelProperty(value = "创建时间")
    private String start_time;

    @ApiModelProperty(value = "结束时间")
    private String end_time;

}
