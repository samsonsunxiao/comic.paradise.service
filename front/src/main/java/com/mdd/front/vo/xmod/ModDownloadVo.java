package com.mdd.front.vo.xmod;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import io.swagger.annotations.ApiModelProperty;

@Data
@ApiModel("MOD下载信息")
public class ModDownloadVo {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "CODE")
    private Integer code;

    @ApiModelProperty(value = "name")
    private String name;
    
    @ApiModelProperty(value = "ModID")
    private String modid;

    @ApiModelProperty(value = "包大小")
    private Long size;

    @ApiModelProperty(value = "下载包链接")
    private String packageUrl;
    
}
