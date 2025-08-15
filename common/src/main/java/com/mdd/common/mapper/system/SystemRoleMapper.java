package com.mdd.common.mapper.system;

import com.mdd.common.core.basics.IBaseMapper;
import com.mdd.common.entity.system.SystemRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统角色Mapper
 */
@Mapper
public interface SystemRoleMapper extends IBaseMapper<SystemRole> {


    @Select("SELECT * FROM la_system_role role INNER JOIN la_admin_role lar ON lar.role_id = role.id WHERE lar.admin_id = #{adminId} AND role.delete_time IS NULL")
    List<SystemRole> getByAdminId(@Param("adminId") Integer adminId);

}
