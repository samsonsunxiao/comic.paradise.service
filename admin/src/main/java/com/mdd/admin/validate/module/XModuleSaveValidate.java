package com.mdd.admin.validate.module;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("Module 保存")
public class XModuleSaveValidate implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @NotEmpty(message = "moduleid不能为空")
    @Length(min = 1, max = 100, message = "moduleid不能大于100个字符")
    @ApiModelProperty(value = "moduleid")
    private String moduleid;
    
    @NotEmpty(message = "名称不能为空")
    @Length(min = 1, max = 256, message = "名称不能大于256个字符")
    @ApiModelProperty(value = "名称", required = true)
    private String name;
    
    @ApiModelProperty(value = "游戏ID集合")
    private List<String> games;
}
