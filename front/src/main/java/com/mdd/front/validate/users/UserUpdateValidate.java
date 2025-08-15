package com.mdd.front.validate.users;

import com.mdd.common.validator.annotation.StringContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("用户更新参数")
public class UserUpdateValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "field参数缺失")
    @StringContains(values = {"avatar", "account", "nickname", "sex"})
    @ApiModelProperty(value = "操作字段", required = true, example = "avatar,account,nickname,sex")
    private String field;

    @NotNull(message = "value参数缺失")
    @ApiModelProperty(value = "变更的值", required = true)
    private String value;

}
