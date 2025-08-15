package com.mdd.common.entity.file;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("文件实体")
public class File implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty("主键ID")
    private Integer id;

    @ApiModelProperty("类目ID")
    private Integer cid;

    @ApiModelProperty("上传者id")
    private Integer sourceId;

    @ApiModelProperty("来源类型[0-后台,1-用户]")
    private Integer source;

    @ApiModelProperty("文件类型: [10=图片, 20=视频]")
    private Integer type;

    @ApiModelProperty("文件名称")
    private String name;

    @ApiModelProperty("文件路径")
    private String uri;

    @ApiModelProperty("创建时间")
    private Long createTime;

    @ApiModelProperty("更新时间")
    private Long updateTime;

    @ApiModelProperty("删除时间")
    private Long deleteTime;

}
