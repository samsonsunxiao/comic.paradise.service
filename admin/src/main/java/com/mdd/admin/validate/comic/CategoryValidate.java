package com.mdd.admin.validate.comic;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("分类 保存")
public class CategoryValidate implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @NotEmpty(message = "名称不能为空")
    @Length(min = 1, max = 256, message = "名称不能大于256个字符")
    @ApiModelProperty(value = "名称", required = true)
    private String title;
    
    @ApiModelProperty(value = "漫画ID集合")
    private List<String> comics;
}
