package com.mdd.common.entity.smsLog;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("短信记录实体")
public class SmsLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty("主键")
    private Integer id;

    @ApiModelProperty("场景")
    private Integer sceneId;

    @ApiModelProperty("手机号码")
    private String mobile;

    @ApiModelProperty("发送内容")
    private String content;

    @ApiModelProperty("发送关键字（注册、找回密码）")
    private String code;

    @ApiModelProperty("是否已验证；0-否；1-是")
    private Integer isVerify;

    @ApiModelProperty("验证次数")
    private Integer checkNum;

    @ApiModelProperty("发送状态：0-发送中；1-发送成功；2-发送失败")
    private Integer sendStatus;

    @ApiModelProperty("发送时间")
    private Long sendTime;

    @ApiModelProperty("短信结果")
    private String results;

    @ApiModelProperty("创建时间")
    private Long createTime;

    @ApiModelProperty("更新时间")
    private Long updateTime;

    @ApiModelProperty("删除时间")
    private Long deleteTime;
}
