package com.mdd.admin.validate.mod;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

@Data
@ApiModel("获取文件大小")
public class GetFileSizeValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "modid")
    private String modid;

    @ApiModelProperty(value = "URL")
    private String url;

}
