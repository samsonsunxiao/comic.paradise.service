package com.mdd.front.validate.users;

import com.mdd.common.validator.annotation.StringContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("用户资金变更参数")
public class UserAccountLogSearchValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer userId;
    private String type;
    private Integer action;
}
