package com.mdd.admin.service.system;

import com.alibaba.fastjson2.JSONArray;
import com.mdd.admin.validate.system.DeptCreateValidate;
import com.mdd.admin.validate.system.DeptSearchValidate;
import com.mdd.admin.validate.system.DeptUpdateValidate;
import com.mdd.admin.vo.system.DeptVo;

import java.util.List;

/**
 * 系统部门服务接口类
 */
public interface IDeptService {

    /**
     * 部门列表
     *
     * @author fzr
     * @param searchValidate 搜索参数
     * @return JSONArray
     */
    JSONArray list(DeptSearchValidate searchValidate);

    /**
     * 部门所有
     *
     * @author fzr
     * @return JSONArray
     */
    JSONArray all();

    /**
     * 部门详情
     *
     * @author fzr
     * @param id 主键
     * @return SysMenu
     */
    DeptVo detail(Integer id);

    /**
     * 部门新增
     *
     * @author fzr
     * @param createValidate 参数
     */
    void add(DeptCreateValidate createValidate);

    /**
     * 部门编辑
     *
     * @author fzr
     * @param updateValidate 参数
     */
    void edit(DeptUpdateValidate updateValidate);

    /**
     * 部门删除
     *
     * @author fzr
     * @param id 主键
     */
    void del(Integer id);

}
