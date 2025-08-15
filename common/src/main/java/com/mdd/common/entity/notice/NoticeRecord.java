package com.mdd.common.entity.notice;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("通知记录实体")
public class NoticeRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty("主键")
    private Integer id;

    @ApiModelProperty("用户")
    private Integer userId;

    @ApiModelProperty("编码")
    private String title;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("场景")
    private Integer sceneId;

    @ApiModelProperty("已读状态: [0=未读, 1=已读]")
    @TableField(value = "`read`")
    private Integer read;

    @ApiModelProperty("通知接收对象类型;1-会员;2-商家;3-平台;4-游客(未注册用户)")
    private Integer recipient;

    @ApiModelProperty("通知发送类型 1-系统通知 2-短信通知 3-微信模板 4-微信小程序")
    private Integer sendType;
    @ApiModelProperty("通知类型 1-业务通知 2-验证码")
    private Integer noticeType;
    @ApiModelProperty("其他")
    private String extra;

    @ApiModelProperty("创建时间")
    private Long createTime;

    @ApiModelProperty("更新时间")
    private Long updateTime;

    @ApiModelProperty("删除时间")
    private Long deleteTime;
}
