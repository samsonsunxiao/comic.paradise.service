package com.mdd.front.validate.xmod;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("下载条件")
public class XModDownloadValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "modid")
    private String modid;

    @ApiModelProperty(value = "软件渠道号")
    private String supply;

    @ApiModelProperty(value = "软件版本号")
    private String version;
}
