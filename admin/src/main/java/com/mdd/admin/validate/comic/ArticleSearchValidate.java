package com.mdd.admin.validate.comic;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

@Data
@ApiModel("漫画文章搜索参数")
public class ArticleSearchValidate implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value = "标题,描述,作者")
    private String text;

    @ApiModelProperty(value = "状态")
    private Integer state;

    @ApiModelProperty(value = "类型")
    private String type;
    
    @ApiModelProperty(value = "漫画状态，offline-下架，online-上架")
    private String status;

}
