package com.mdd.admin.validate.vip;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("等级创建参数")
public class LevelSaveValidate implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value = "ID", required = true)
    private String keyid;
    
    @ApiModelProperty(value = "标题")
    private String title = "";

    @ApiModelProperty(value = "等级值")
    private Integer value = 0;

    @ApiModelProperty(value = "权益列表")
    private List<String> rights;

    @ApiModelProperty(value = "支付模式列表")
    private List<String>  pays;
   
}
