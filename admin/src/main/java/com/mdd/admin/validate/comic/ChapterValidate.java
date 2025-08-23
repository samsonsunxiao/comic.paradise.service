package com.mdd.admin.validate.comic;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

import javax.validation.constraints.NotEmpty;

@Data
@ApiModel("章节 保存")
public class ChapterValidate implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value = "章节索引")
    private Integer no = 0;

    @NotEmpty(message = "标题不能为空")
    @ApiModelProperty(value = "标题")
    private String title;

    @NotEmpty(message = "漫画ID不能为空")
    @ApiModelProperty(value = "漫画ID")
    private String comicId = "";

    @ApiModelProperty(value = "是否需要VIP")
    private Boolean vip = false;

}
