package com.mdd.common.entity.vip;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@ApiModel("VIP 等级")
@TableName(value = "xmod_vip_level") 
public class VipLevel implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "keyid")
    private String keyid;

    @ApiModelProperty(value = "title")
    private String title;

    @ApiModelProperty(value = "value")
    private Integer value;
    
}
