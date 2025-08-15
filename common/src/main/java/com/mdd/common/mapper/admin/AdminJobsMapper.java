package com.mdd.common.mapper.admin;

import com.mdd.common.core.basics.IBaseMapper;
import com.mdd.common.entity.admin.AdminJobs;
import com.mdd.common.entity.admin.AdminRole;
import com.mdd.common.entity.system.SystemRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色关联表Mapper
 */
@Mapper
public interface AdminJobsMapper extends IBaseMapper<AdminJobs> {



}
