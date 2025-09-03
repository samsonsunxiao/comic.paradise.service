package com.mdd.admin.service.comic;

import java.util.List;

import com.mdd.common.entity.comic.Category;
import com.mdd.admin.validate.comic.CategoryValidate;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.vo.comic.CategoryDetailVo;
import com.mdd.admin.vo.comic.CategoryListVo;
import com.mdd.common.core.PageResult;


/**
 * MOD服务接口类
 */
public interface ICategoryService {
    /**
    * 所有模块列表
    */
    List<Category> all();
    
    /**
     * MOD资源列表
     *
     * @param pageValidate   分页参数
     * @param searchValidate 搜索参数
     * @return PageResult<ArticleListVo>
     * @author fzr
     */
    PageResult<CategoryListVo> list(PageValidate pageValidate);

    /**
     * module详情
     *
     * @param moduleid 
     * @author fzr
     */
    CategoryDetailVo detail(String category_id);
    
     /**
     * MODULE编辑保存
     *
     * @param saveValidate 参数
     * @author fzr
     */
    void commitBatchComics(CategoryValidate saveValidate);

    void commitSingleComic(CategoryValidate saveValidate, String comicId);

    void del(Integer id);
}
