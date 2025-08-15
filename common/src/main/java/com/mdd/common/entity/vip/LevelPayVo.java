package com.mdd.common.entity.vip;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("VIP 等级与支付模式关系")
public class LevelPayVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "level")
    private String level;

    @ApiModelProperty(value = "paymodel")
    private String paymodel;

    @ApiModelProperty(value = "title")
    private String title;

    @ApiModelProperty(value = "price")
    private BigDecimal price;

    @ApiModelProperty(value = "image")
    private String image;
}
