package com.mdd.common.entity.comic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@ApiModel("Tag")
@TableName(value = "comic_tag") 
public class ComicTag implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "标签ID")
    private String tagId;

    @ApiModelProperty(value = "标题")
    private String title;
}