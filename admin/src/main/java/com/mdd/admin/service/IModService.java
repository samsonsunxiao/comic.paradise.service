package com.mdd.admin.service;

import com.mdd.admin.vo.mod.XModDetailVo;
import com.mdd.admin.vo.mod.XModListedVo;
import com.mdd.admin.vo.mod.XModOfflineVo;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.mod.XModSaveValidate;
import com.mdd.admin.validate.mod.XModSearchValidate;
import com.mdd.common.core.PageResult;


/**
 * MOD服务接口类
 */
public interface IModService {

    /**
     * MOD资源列表
     *
     * @param pageValidate   分页参数
     * @param searchValidate 搜索参数
     * @return PageResult<ArticleListVo>
     * @author fzr
     */
    PageResult<XModListedVo> list(PageValidate pageValidate, XModSearchValidate searchValidate);

    /**
     * 资源详情
     *
     * @param id 主键ID
     * @author fzr
     */
    XModDetailVo detail(String modid);
    
     /**
     * MOD编辑保存
     *
     * @param saveValidate 参数
     * @author fzr
     */
    void save(XModSaveValidate saveValidate);

    XModOfflineVo getOneOffline();
    
    void removeTemp(String modid);
}
