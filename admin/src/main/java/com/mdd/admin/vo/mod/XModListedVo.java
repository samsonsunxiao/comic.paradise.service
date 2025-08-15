package com.mdd.admin.vo.mod;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


@Data
@ApiModel("资源列表Vo")
public class XModListedVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Integer id;
    
    @ApiModelProperty(value = "MODID")
    private String modid;
    
    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "详情链接")
    private String detailUrl;

    @ApiModelProperty("所属游戏")
    private String game;
    
    @ApiModelProperty("缩略图")
    private String thumbImage = "";
    
    @ApiModelProperty(value = "描述")
    private String descript;

    @ApiModelProperty(value = "来源")
    private String platform = "";

    @ApiModelProperty(value = "发布日期")
    private String date = "";

    @ApiModelProperty(value = "状态")
    private String status;

}
