package com.mdd.admin.service.admin;

import com.mdd.common.entity.admin.AdminRole;

import java.util.List;

/**
 * 角色关联接口类
 */
public interface IAdminRoleService {

    /**
     * 关联角色id
     *
     * @author damonyuan
     */
    List<Integer> getRoleIdAttr(Integer adminId);


    List<AdminRole> getAdminIdByRoleId(Integer roleId);


    /**
     * 批量设置部门
     * @param adminId
     * @param deptIds
     */
    void batchInsert(Integer adminId, List<Integer> deptIds);

    /**
     * 删除关联角色
     * @param adminId
     */
    void deleteByAdminId(Integer adminId);
}
