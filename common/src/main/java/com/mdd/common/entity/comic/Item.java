package com.mdd.common.entity.comic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@ApiModel("Item")
@TableName(value = "comic_item") 
public class Item implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "图片的URI")
    private String uri;

    @ApiModelProperty(value = "漫画ID")
    private String comicId;

    @ApiModelProperty(value = "章节码")
    private String chapterNo;
}