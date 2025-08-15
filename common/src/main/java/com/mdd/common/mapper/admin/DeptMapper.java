package com.mdd.common.mapper.admin;

import com.mdd.common.core.basics.IBaseMapper;
import com.mdd.common.entity.admin.Dept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 部门表Mapper
 */
@Mapper
public interface DeptMapper extends IBaseMapper<Dept> {

    @Select("SELECT * FROM la_dept dept INNER JOIN la_admin_dept lad ON lad.dept_id = dept.id WHERE lad.admin_id = #{adminId} AND dept.delete_time IS NULL")
    List<Dept> getByAdminId(@Param("adminId") Integer adminId);

}
