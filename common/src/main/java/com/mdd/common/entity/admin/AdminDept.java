package com.mdd.common.entity.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;

@Data
@ApiModel("部门关联表实体")
public class AdminDept implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="admin_id")
    @ApiModelProperty("管理员id")
    private Integer adminId;

    @ApiModelProperty("部门id")
    private Integer deptId;


}
