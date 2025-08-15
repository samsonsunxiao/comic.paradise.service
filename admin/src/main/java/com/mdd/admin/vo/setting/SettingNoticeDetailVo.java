package com.mdd.admin.vo.setting;

import com.alibaba.fastjson2.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("通知设置详情Vo")
public class SettingNoticeDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "senceId")
    private Integer senceId;

    @ApiModelProperty(value = "场景名称")
    private String sceneName;

    @ApiModelProperty(value = "通知类型: [1=业务, 2=验证]")
    private String type;

    @ApiModelProperty(value = "场景描述")
    private String sceneDesc;

    @ApiModelProperty(value = "系统的通知设置")
    private JSONObject systemNotice;

    @ApiModelProperty(value = "公众号通知设置")
    private JSONObject oaNotice;

    @ApiModelProperty(value = "小程序通知设置")
    private JSONObject mnpNotice;

    @ApiModelProperty(value = "短信的通知设置")
    private JSONObject smsNotice;

}
