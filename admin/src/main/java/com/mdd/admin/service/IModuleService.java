package com.mdd.admin.service;

import com.mdd.admin.vo.module.XModuleDetailVo;
import com.mdd.admin.vo.module.XModuleListVo;

import java.util.List;

import com.mdd.common.entity.module.XModule;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.module.XModuleSaveValidate;
import com.mdd.common.core.PageResult;


/**
 * MOD服务接口类
 */
public interface IModuleService {
    /**
    * 所有模块列表
    */
    List<XModule> all();
    
    /**
     * MOD资源列表
     *
     * @param pageValidate   分页参数
     * @param searchValidate 搜索参数
     * @return PageResult<ArticleListVo>
     * @author fzr
     */
    PageResult<XModuleListVo> list(PageValidate pageValidate);

    /**
     * module详情
     *
     * @param moduleid 
     * @author fzr
     */
    XModuleDetailVo detail(String moduleid);
    
     /**
     * MODULE编辑保存
     *
     * @param saveValidate 参数
     * @author fzr
     */
    void save(XModuleSaveValidate saveValidate);

    void del(Integer id);
}
