package com.mdd.admin.validate.vip;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("支付模式创建参数")
public class PaySaveValidate implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value = "名称", required = true)
    private String keyid;
    
    @ApiModelProperty(value = "标题")
    private String title = "";

    @ApiModelProperty(value = "价格")
    private BigDecimal price;
   
    @ApiModelProperty(value = "图片")
    private String image = "";

    @ApiModelProperty(value = "备注")
    private String remark;
}
