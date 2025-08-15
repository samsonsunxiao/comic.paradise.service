package com.mdd.common.entity.mod;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@ApiModel("MOD资源文件关系表")
@TableName(value = "xmod_files") 
public class XModFiles implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "uid")
    private String uid;
    
    @ApiModelProperty(value = "文件名")
    private String filename;
    
    @ApiModelProperty(value = "URL")
    private String url;
    
    @ApiModelProperty(value = "资源uri")
    private String keyuri;
    
    @ApiModelProperty(value = "大小")
    private long size;
    
    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "状态")
    private String status;
    
    @ApiModelProperty(value = "描述")
    private String descript;
}
