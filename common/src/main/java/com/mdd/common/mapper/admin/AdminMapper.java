package com.mdd.common.mapper.admin;

import com.mdd.common.core.basics.IBaseMapper;
import com.mdd.common.entity.admin.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统管理员Mapper
 */
@Mapper
public interface AdminMapper extends IBaseMapper<Admin> {


    @Select("SELECT * FROM la_admin admin INNER JOIN la_admin_dept lad ON admin.id = lad.admin_id WHERE lad.dept_id = #{deptId} AND admin.delete_time IS NULL")
    List<Admin> getByDept(@Param("deptId") Integer deptId);

    @Select("SELECT * FROM la_admin admin INNER JOIN la_admin_jobs lad ON admin.id = lad.admin_id WHERE lad.jobs_id = #{jobId} AND admin.delete_time IS NULL")
    List<Admin> getByJobs(@Param("jobId") Integer jobId);


    @Select("SELECT count(*) FROM la_admin admin INNER JOIN la_admin_role lad ON admin.id = lad.admin_id WHERE lad.role_id = #{roleId} AND admin.delete_time IS NULL")
    Integer getCountByRoleId(@Param("roleId") Integer roleId);


}
