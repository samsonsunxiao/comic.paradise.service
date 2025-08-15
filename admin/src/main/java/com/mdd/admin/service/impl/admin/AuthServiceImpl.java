package com.mdd.admin.service.impl.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mdd.admin.service.admin.IAdminDeptService;
import com.mdd.admin.service.admin.IAdminService;
import com.mdd.admin.service.admin.IAuthService;
import com.mdd.admin.vo.system.SystemAuthAdminDetailVo;
import com.mdd.common.entity.admin.AdminDept;
import com.mdd.common.mapper.admin.AdminDeptMapper;
import com.mdd.common.mapper.system.SystemMenuMapper;
import com.mdd.common.mapper.system.SystemRoleMenuMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 权限功能类
 */
@Service
public class AuthServiceImpl implements IAuthService {

    @Resource
    IAdminService iAdminService;

    @Resource
    SystemMenuMapper systemMenuMapper;

    @Resource
    SystemRoleMenuMapper SystemRoleMenuMapper;

    @Override
    public List<String> getBtnAuthByRoleId(Integer adminId) {
        List<String> ret = new ArrayList<String>();
        SystemAuthAdminDetailVo admin = iAdminService.detail(adminId);
        if (admin.getRoot().equals(1)) {
            ret.add("*");
            return ret;
        } else {
            List<Integer> menuIds = SystemRoleMenuMapper.getMenuIds(adminId);
            ret = this.systemMenuMapper.getPerms(menuIds);
        }
        return ret;
    }
}
