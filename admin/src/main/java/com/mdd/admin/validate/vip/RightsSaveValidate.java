package com.mdd.admin.validate.vip;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigInteger;

@Data
@ApiModel("权益创建参数")
public class RightsSaveValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "名称", required = true)
    private String keyid;
    
    @ApiModelProperty(value = "标题")
    private String title = "";

    @ApiModelProperty(value = "描述")
    private String descript = "";

    @ApiModelProperty(value = "数值")
    private BigInteger value;

    @ApiModelProperty(value = "icon")
    private String icon;
   
    @ApiModelProperty(value = "权益类型")
    private String type = "";
}
