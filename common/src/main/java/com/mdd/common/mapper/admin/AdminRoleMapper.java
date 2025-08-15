package com.mdd.common.mapper.admin;

import com.mdd.common.core.basics.IBaseMapper;
import com.mdd.common.entity.admin.Admin;
import com.mdd.common.entity.admin.AdminRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色关联表Mapper
 */
@Mapper
public interface AdminRoleMapper extends IBaseMapper<AdminRole> {

}
