package com.mdd.admin.vo.setting;

import com.alibaba.fastjson2.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("通知设置列表Vo")
public class SettingNoticeListedVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "通知名称")
    private String sceneName;

    @ApiModelProperty(value = "通知类型")
    private String type;

    @ApiModelProperty(value = "通知类型")
    private String typeDesc;

    @ApiModelProperty(value = "通知状态")
    private String smsStatusDesc;

    @ApiModelProperty(value = "通知对象")
    private JSONObject smsNotice;

}
