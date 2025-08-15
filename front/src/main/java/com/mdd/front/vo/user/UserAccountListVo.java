package com.mdd.front.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "用户资金变更Vo")
public class UserAccountListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户ID")
    private Integer id;
    @ApiModelProperty("变动类型")
    private Integer changeType;

    @ApiModelProperty("变动类型字符串")
    private String typeDesc;

    @ApiModelProperty("变动数量")
    private BigDecimal changeAmount;

    @ApiModelProperty("变动数量字符串")
    private String changeAmountDesc;

    @ApiModelProperty("变动类型: [1=增加, 2=减少]")
    private Integer action;
    @ApiModelProperty("备注信息")
    private String remark;

    @ApiModelProperty("创建时间")
    private String createTime;


}
