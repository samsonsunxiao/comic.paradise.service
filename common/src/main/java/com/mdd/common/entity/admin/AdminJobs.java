package com.mdd.common.entity.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

@Data
@ApiModel("岗位关联表实体")
public class AdminJobs implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="admin_id")
    @ApiModelProperty("管理员id")
    private Integer adminId;

    @ApiModelProperty("岗位id")
    private Integer jobsId;


}
