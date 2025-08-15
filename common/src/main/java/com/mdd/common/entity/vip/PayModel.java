package com.mdd.common.entity.vip;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@ApiModel("VIP 等级")
@TableName(value = "xmod_pay_model") 
public class PayModel implements Serializable {

    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "keyid")
    private String keyid;

    @ApiModelProperty(value = "title")
    private String title;

    @ApiModelProperty(value = "price")
    private BigDecimal price;

    @ApiModelProperty(value = "image")
    private String image;

    @ApiModelProperty(value = "remark")
    private String remark;
}
