package com.mdd.admin.service.system;

import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.system.SystemRoleCreateValidate;
import com.mdd.admin.validate.system.SystemRoleUpdateValidate;
import com.mdd.admin.vo.system.SystemAuthRoleVo;
import com.mdd.common.core.PageResult;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * 系统角色服务接口类
 */
public interface ISystemRoleService {

    /**
     * 角色所有
     *
     * @author fzr
     * @return List<SystemAuthRoleVo>
     */
    List<SystemAuthRoleVo> all();

    /**
     * 角色列表
     *
     * @author fzr
     * @param pageValidate 参数
     * @return PageResult<SysRoleListVo>
     */
    PageResult<SystemAuthRoleVo> list(@Validated PageValidate pageValidate);

    /**
     * 角色详情
     *
     * @author fzr
     * @param id 主键参数
     * @return SysRole
     */
    SystemAuthRoleVo detail(Integer id);

    /**
     * 角色新增
     *
     * @author fzr
     * @param createValidate 参数
     */
    void add(SystemRoleCreateValidate createValidate);

    /**
     * 角色更新
     *
     * @author fzr
     * @param updateValidate 参数
     */
    void edit(SystemRoleUpdateValidate updateValidate);

    /**
     * 角色删除
     *
     * @author fzr
     * @param id 主键参数
     */
    void del(Integer id);

    /**
     * 根据adminID 返回角色名称
     * @param adminId
     */
    List<String> getRoleNameByAdminId(Integer adminId);

}
