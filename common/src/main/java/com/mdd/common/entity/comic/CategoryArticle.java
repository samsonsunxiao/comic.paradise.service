package com.mdd.common.entity.comic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@ApiModel("分类和漫画关系表")
@TableName(value = "comic_category_article") 
public class CategoryArticle implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "分类ID")
    private String categoryId;

    @ApiModelProperty(value = "漫画ID")
    private String comicId;
}
