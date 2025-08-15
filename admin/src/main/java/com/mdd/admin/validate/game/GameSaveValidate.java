package com.mdd.admin.validate.game;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import com.mdd.common.entity.mod.XModImages;
import com.mdd.common.entity.mod.XModFiles;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("Game资源创建参数")
public class GameSaveValidate implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value = "modid")
    private String modid;
    
    @NotEmpty(message = "gid不能为空")
    @Length(min = 1, max = 100, message = "gid不能大于200个字符")
    @ApiModelProperty(value = "gid", required = true)
    private String gid;
    
    @NotEmpty(message = "标题不能为空")
    @Length(min = 1, max = 256, message = "标题不能大于200个字符")
    @ApiModelProperty(value = "标题", required = true)
    private String name;
    
    @ApiModelProperty(value = "封面")
    private String coverImage = "";

    @ApiModelProperty(value = "描述")
    private String descript = "";

    @ApiModelProperty(value = "游戏图片列表")
    private List<XModImages> images;

    @ApiModelProperty(value = "游戏图片列表")
    private List<XModFiles>  managers;
    
    @ApiModelProperty(value = "评分")
    private Float score = 0.0f;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "模块ID集合")
    private List<String>  modules;

    @ApiModelProperty(value = "上架时间")
    private String onlineTime;

    @ApiModelProperty(value = "banner广告语")
    private String banner;
}
