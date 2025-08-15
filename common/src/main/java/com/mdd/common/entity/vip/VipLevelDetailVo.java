package com.mdd.common.entity.vip;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.List;
@Data
@ApiModel("等级列表Vo")
public class VipLevelDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "name")
    private String name;

    @ApiModelProperty(value = "title")
    private String title;

    @ApiModelProperty(value = "value")
    private Integer value;

    @ApiModelProperty(value = "rights")
    List<VipRights>  rights;

    @ApiModelProperty(value = "pays")
    List<PayModel>  pays;
}
