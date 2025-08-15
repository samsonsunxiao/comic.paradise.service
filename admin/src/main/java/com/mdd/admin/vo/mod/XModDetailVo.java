package com.mdd.admin.vo.mod;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.mdd.common.entity.mod.XModFiles;
import com.mdd.common.entity.mod.XModImages;


@Data
@ApiModel("资源列表Vo")
public class XModDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "标题")
    private String title = "";

    @ApiModelProperty(value = "modid")
    private String modid = "";
    
    @ApiModelProperty(value = "详情链接")
    private String detailUrl = "";
    
    @ApiModelProperty("所属游戏")
    private String game = "";

    @ApiModelProperty("所属游戏ID")
    private String gid = "";

    @ApiModelProperty(value = "来源")
    private String platform = "";

    @ApiModelProperty(value = "资源图片")
    private List<XModImages>  images;

    @ApiModelProperty(value = "资源包文件")
    private List<XModFiles>  files;

    @ApiModelProperty(value = "前置修改器包")
    private List<Map<String,Object>>  managers;
    
    @ApiModelProperty(value = "最终组合包")
    private XModFiles  combinPackage;
    
    @ApiModelProperty(value = "描述")
    private String descript = "";

    @ApiModelProperty(value = "安装描述")
    private String installdesc = "";

    @ApiModelProperty(value = "发布日期")
    private String date = "";
    
    @ApiModelProperty(value = "源ID")
    private Integer srcId;
    
    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "是否需要VIP下载")
    private Boolean vip;
}
