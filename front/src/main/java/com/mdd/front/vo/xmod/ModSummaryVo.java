package com.mdd.front.vo.xmod;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


@Data
@ApiModel("MOD概略详情")
public class ModSummaryVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "name")
    private String name;
    
    @ApiModelProperty(value = "ModID")
    private String modid;
    
    @ApiModelProperty(value = "热度")
    private Integer hot;
    
    @ApiModelProperty(value = "缩略图")
    private String image;

    @ApiModelProperty(value = "包大小")
    private Long size;

    // @ApiModelProperty(value = "下载包链接")
    // private String packageUrl;
    
    @ApiModelProperty(value = "上架时间")
    private String onlineTime;

    @ApiModelProperty(value = "是否需要VIP下载")
    private Boolean vip;
}
