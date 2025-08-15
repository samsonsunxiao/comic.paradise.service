package com.mdd.front.vo.decorateTabbar;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "底部导航服务Vo")
public class DecorateTabbarVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("导航名称")
    private String name;

    @ApiModelProperty("未选图标")
    private String selected;

    @ApiModelProperty("已选图标")
    private String unselected;

    @ApiModelProperty("链接地址")
    private JSONObject link;

    @ApiModelProperty("是否显示")
    private Integer isShow;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("更新时间")
    private String updateTime;


}
