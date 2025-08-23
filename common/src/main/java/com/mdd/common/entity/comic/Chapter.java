package com.mdd.common.entity.comic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;


@Data
@ApiModel("漫画章节")
@TableName(value = "comic_chapter") 
public class Chapter implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "章节索引")
    private Integer no = 0;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "漫画ID")
    private String comicId = "";

    @ApiModelProperty(value = "是否需要VIP")
    private Boolean vip;
}
