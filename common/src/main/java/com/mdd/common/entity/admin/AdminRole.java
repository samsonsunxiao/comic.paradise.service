package com.mdd.common.entity.admin;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("角色关联表实体")
public class AdminRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="admin_id")
    @ApiModelProperty("管理员id")
    private Integer adminId;

    @ApiModelProperty("角色id")
    private Integer roleId;


}
