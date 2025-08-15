package com.mdd.admin.vo.album;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("文件Vo")
public class FileVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "所属类目")
    private Integer cid;

    @ApiModelProperty(value = "类型")
    private Integer type;

    @ApiModelProperty(value = "文件名称")
    private String name;

    @ApiModelProperty(value = "文件路径")
    private String url;

    @ApiModelProperty(value = "相对路径")
    private String uri;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "更新时间")
    private String updateTime;

}
