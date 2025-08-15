package com.mdd.admin.service.impl.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mdd.admin.service.admin.IAdminJobsService;
import com.mdd.admin.service.admin.IAdminRoleService;
import com.mdd.common.entity.admin.AdminJobs;
import com.mdd.common.entity.admin.AdminRole;
import com.mdd.common.mapper.admin.AdminJobsMapper;
import com.mdd.common.mapper.admin.AdminRoleMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统岗位实现类
 */
@Service
public class AdminJobsServiceImpl implements IAdminJobsService {

    @Resource
    AdminJobsMapper adminJobsMapper;
    @Override
    public List<Integer> getJobIdAttr(Integer adminId) {
        List<Integer> ret = new ArrayList<Integer>();
        List<AdminJobs> rolesList = adminJobsMapper.selectList(new QueryWrapper<AdminJobs>().eq("admin_id", adminId).select("jobs_id"));
        if (rolesList.size() > 0) {
            for (AdminJobs adminJobs : rolesList) {
                ret.add(adminJobs.getJobsId());
            }
        }
        return ret;
    }

    @Override
    public void batchInsert(Integer adminId, List<Integer> jobsIds) {
        this.deleteByAdminId(adminId);
        if (jobsIds != null && !jobsIds.isEmpty()) {
            jobsIds.forEach(item-> {
                this.adminJobsMapper.insert(new AdminJobs(){{
                    setAdminId(adminId);
                    setJobsId(item);
                }});
            });
        }
    }

    @Override
    public void deleteByAdminId(Integer adminId) {
        this.adminJobsMapper.delete(new QueryWrapper<AdminJobs>().eq("admin_id", adminId));
    }
}
