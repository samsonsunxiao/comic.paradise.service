package com.mdd.common.entity.vip;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("VIP 等级与权益关系表")
public class LevelRightsVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "level")
    private String level;

    @ApiModelProperty(value = "rights")
    private String rights;

    @ApiModelProperty(value = "权益的标题")
    private String title;

    @ApiModelProperty(value = "权益的数值")
    private Integer rightvalue;

    @ApiModelProperty(value = "权益的类型")
    private String righttype;

    @ApiModelProperty(value = "descript")
    private String descript;
   
    @ApiModelProperty(value = "icon")
    private String icon;
}
