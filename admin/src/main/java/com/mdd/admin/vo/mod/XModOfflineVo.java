package com.mdd.admin.vo.mod;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;



@Data
@ApiModel("线下未上传Vo")
public class XModOfflineVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "MODID")
    private String modid = "";

    @ApiModelProperty(value = "MOD名称")
    private String name = "";

    @ApiModelProperty(value = "原始URL")
    private String orgurl = "";

    @ApiModelProperty(value = "目标URI")
    private String keyuri = "";
}
