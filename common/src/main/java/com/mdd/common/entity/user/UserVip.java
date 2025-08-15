package com.mdd.common.entity.user;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("用户会员关系")
@TableName(value = "xmod_vip_user") 
public class UserVip implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("会员值")
    private Integer vip;

    @ApiModelProperty("用户ID")
    private Integer userId;

    @ApiModelProperty("用户充值模式")
    private String payModel;

    @ApiModelProperty("过期时间")
    private long expireTime;

    @ApiModelProperty("会员状态")
    private Integer status;

    @ApiModelProperty("最后一次下载时间")
    private long lasttime;
    
    @ApiModelProperty("会员数据")
    private String data;
}
