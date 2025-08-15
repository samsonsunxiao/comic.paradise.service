package com.mdd.admin.validate.mod;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import com.mdd.common.entity.mod.XModFiles;
import com.mdd.common.entity.mod.XModImages;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("MOD资源创建参数")
public class XModSaveValidate implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value = "modid")
    private String modid;
    
    @NotEmpty(message = "标题不能为空")
    @Length(min = 1, max = 256, message = "标题不能大于200个字符")
    @ApiModelProperty(value = "标题", required = true)
    private String title;
    
    @ApiModelProperty(value = "状态")
    private String status;
    
    @ApiModelProperty(value = "是否需要VIP下载")
    private Boolean vip;

    @ApiModelProperty(value = "游戏id")
    private String gid = "";

    @ApiModelProperty(value = "游戏")
    private String game = "";

    @ApiModelProperty(value = "日期")
    private String date = "";

    @ApiModelProperty(value = "描述")
    private String descript = "";

    @ApiModelProperty(value = "安装描述")
    private String installdesc = "";

    @ApiModelProperty(value = "MOD图片列表")
    private List<XModImages> images;

    @ApiModelProperty(value = "MOD文件列表")
    private List<XModFiles> files;

    @ApiModelProperty(value = "最终组合包")
    private XModFiles combinPackage;
}
