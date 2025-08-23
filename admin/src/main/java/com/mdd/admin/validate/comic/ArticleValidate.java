package com.mdd.admin.validate.comic;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("漫画文章 保存")
public class ArticleValidate implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value = "文章ID")
    private String comicId;
    
    @NotEmpty(message = "名称不能为空")
    @Length(min = 1, max = 256, message = "名称不能大于256个字符")
    @ApiModelProperty(value = "名称", required = true)
    private String title;

    @ApiModelProperty(value = "描述")
    private String descript = "";

    @ApiModelProperty(value = "作者")
    private String author;

    @ApiModelProperty(value = "封面")
    private String coverImage;

    @ApiModelProperty(value = "状态")
    private Integer state;

    @ApiModelProperty(value = "类型")
    private String type;
    
    @ApiModelProperty(value = "漫画状态，offline-下架，online-上架")
    private String status;

    @ApiModelProperty(value = "是否需要VIP下载")
    private Boolean vip;
    
    @ApiModelProperty(value = "章节集合")
    private List<ChapterValidate> chapters;
}
