package com.mdd.common.entity.vip;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@ApiModel("VIP 等级")
@TableName(value = "xmod_vip_rights") 
public class VipRights implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty(value = "keyid")
    private String keyid;

    @ApiModelProperty(value = "标题")
    private String title = "";

    @ApiModelProperty(value = "权益类型")
    private String type = "";

    @ApiModelProperty(value = "value")
    private BigInteger value;
    
    @ApiModelProperty(value = "descript")
    private String descript;

    @ApiModelProperty(value = "icon")
    private String icon;
}
