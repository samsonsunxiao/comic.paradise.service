package com.mdd.common.entity.mod;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;


@Data
@ApiModel("MOD详情")
@TableName(value = "xmod_store") 
public class XMod implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "modid")
    private String modid;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "发布日期")
    private Long onlineTime = 0L;

    @ApiModelProperty(value = "来源")
    private String platform = "";

    @ApiModelProperty("详情链接")
    private String detailUrl = "";

    @ApiModelProperty("所属游戏ID")
    private String gid = "";

    @ApiModelProperty("所属游戏")
    private String game = "";

    @ApiModelProperty(value = "描述")
    private String descript = "";

    @ApiModelProperty(value = "安装描述")
    private String installdesc = "";

    @ApiModelProperty(value = "源ID")
    private Integer srcId;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "是否需要VIP下载")
    private Boolean vip;
}
