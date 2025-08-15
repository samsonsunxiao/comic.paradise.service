package com.mdd.admin.vo.setting;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("政策协议详情Vo")
public class SettingAgreementVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "服务协议标题")
    private String serviceTitle;

    @ApiModelProperty(value = "服务协议内容")
    private String serviceContent;

    @ApiModelProperty(value = "服务协议标题")
    private String privacyTitle;

    @ApiModelProperty(value = "服务协议内容")
    private String privacyContent;

}
