package com.mdd.admin.validate.setting;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("站点统计设置参数")
public class SettingSiteStatisticsValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "clarityCode")
    private String clarityCode;

}
