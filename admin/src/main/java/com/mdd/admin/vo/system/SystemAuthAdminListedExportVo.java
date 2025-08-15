package com.mdd.admin.vo.system;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("管理员导出列表Vo")
public class SystemAuthAdminListedExportVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @ExcelProperty("主键")
    @ColumnWidth(50)
    private Integer id;

    @ApiModelProperty(value = "账号")
    @ExcelProperty("账号")
    @ColumnWidth(50)
    private String account;

    @ApiModelProperty(value = "昵称")
    @ExcelProperty("昵称")
    @ColumnWidth(50)
    private String name;

    @ApiModelProperty(value = "头像")
    @ExcelProperty("头像")
    @ColumnWidth(50)
    private String avatar;

    @ApiModelProperty(value = "部门")
    @ExcelProperty("部门")
    @ColumnWidth(50)
    private String deptName;

    @ApiModelProperty(value = "部门")
    @ExcelProperty("部门")
    @ColumnWidth(50)
    private String roleName;

    @ApiModelProperty(value = "部门")
    @ExcelProperty("部门")
    @ColumnWidth(50)
    private String jobsName;

    @ApiModelProperty(value = "角色ID")
    @ExcelProperty("角色ID")
    @ColumnWidth(50)
    private List<Integer> roleId;

    @ApiModelProperty(value = "部门ID")
    @ExcelProperty("部门ID")
    @ColumnWidth(50)
    private List<Integer> deptId;

    @ApiModelProperty(value = "岗位ID")
    @ExcelProperty("ID")
    @ColumnWidth(50)
    private List<Integer> jobsId;

    @ApiModelProperty(value = "多端登录: [0=否, 1=是]")
    @ExcelProperty("多端登录: [0=否, 1=是]")
    @ColumnWidth(50)
    private Integer multipointLogin;

    @ApiModelProperty(value = "是否禁用: [0=否, 1=是]")
    @ExcelProperty("是否禁用: [0=否, 1=是]")
    @ColumnWidth(50)
    private Integer disable;

    @ApiModelProperty(value = "是否禁用")
    @ExcelProperty("是否禁用")
    @ColumnWidth(50)
    private String disableDesc;

    @ApiModelProperty(value = "root")
    @ExcelProperty("root")
    @ColumnWidth(50)
    private Integer root;

    @ApiModelProperty(value = "最后登录IP")
    @ExcelProperty("最后登录IP")
    @ColumnWidth(50)
    private String loginIp;

    @ApiModelProperty(value = "最后登录时间")
    @ExcelProperty("最后登录时间")
    @ColumnWidth(50)
    private String loginTime;

    @ApiModelProperty(value = "创建时间")
    @ExcelProperty("创建时间")
    @ColumnWidth(50)
    private String createTime;

    @ApiModelProperty(value = "更新时间")
    @ExcelProperty("更新时间")
    @ColumnWidth(50)
    private String updateTime;

}
