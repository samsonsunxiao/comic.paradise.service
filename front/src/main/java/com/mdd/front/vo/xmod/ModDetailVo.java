package com.mdd.front.vo.xmod;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


@Data
@ApiModel("MOD详情")
public class ModDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "modid")
    private String modid;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "发布日期")
    private String date = "";

    @ApiModelProperty("所属游戏ID")
    private String gid = "";

    @ApiModelProperty("所属游戏")
    private String game = "";

    @ApiModelProperty("图片")
    private String image = "";

    @ApiModelProperty(value = "描述")
    private String descript = "";

    @ApiModelProperty(value = "安装描述")
    private String installdesc = "";
    
    
}
