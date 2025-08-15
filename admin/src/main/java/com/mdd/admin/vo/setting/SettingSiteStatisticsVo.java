package com.mdd.admin.vo.setting;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("站点统计配置信息Vo")
public class SettingSiteStatisticsVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "clarityCode")
    private String clarityCode;

}
