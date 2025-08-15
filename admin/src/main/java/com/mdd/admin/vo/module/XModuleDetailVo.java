package com.mdd.admin.vo.module;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

import com.mdd.admin.vo.game.GameNameVo;

@Data
@ApiModel("Module")
public class XModuleDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "modid")
    private String moduleid;

    @ApiModelProperty(value = "名称")
    private String name;

     @ApiModelProperty(value = "游戏列表")
    private List<GameNameVo>  games;
}