package com.mdd.admin.vo.system;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("系统岗位导出Vo")
public class JobsExportVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @ExcelProperty("ID")
    @ColumnWidth(50)
    private Integer id;

    @ApiModelProperty(value = "岗位编号")
    @ExcelProperty("岗位编号")
    @ColumnWidth(50)
    private String code;

    @ApiModelProperty(value = "岗位名称")
    @ExcelProperty("岗位名称")
    @ColumnWidth(50)
    private String name;

    @ApiModelProperty(value = "岗位备注")
    @ExcelProperty("岗位备注")
    @ColumnWidth(50)
    private String remark;

    @ApiModelProperty(value = "岗位排序")
    @ExcelProperty("岗位排序")
    @ColumnWidth(50)
    private Integer sort;

    @ApiModelProperty(value = "是否停用: [0=否, 1=是]")
    @ExcelProperty("是否停用: [0=否, 1=是]")
    @ColumnWidth(50)
    private Integer status;

    @ApiModelProperty(value = "状态描述")
    @ExcelProperty("状态描述")
    @ColumnWidth(50)
    private String statusDesc;

    @ApiModelProperty(value = "创建时间")
    @ExcelProperty("创建时间")
    @ColumnWidth(50)
    private String createTime;

    @ApiModelProperty(value = "更新时间")
    @ExcelProperty("更新时间")
    @ColumnWidth(50)
    private String updateTime;

}
