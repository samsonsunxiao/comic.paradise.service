package com.mdd.front.vo.user;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("用户会员关系")
public class UserVipVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("会员值")
    private Integer vip;

    @ApiModelProperty("会员名称")
    private String name;

    @ApiModelProperty("会员标题")
    private String title;

    @ApiModelProperty("付款方式")
    private String payModel;

    @ApiModelProperty("过期天数")
    private long expireDate;
}
