package com.mdd.admin.service.comic;

import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.comic.ArticleValidate;
import com.mdd.admin.validate.comic.ArticleSearchValidate;
import com.mdd.admin.vo.comic.SummaryVo;
import com.mdd.admin.vo.comic.ArticleListVo;
import com.mdd.admin.vo.comic.ArticleDetailVo;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.comic.ComicItem;

import java.util.*;

public interface IComicArticleService {


    /**
    * 所有漫画列表
    */
    List<SummaryVo>  all(String status);
    
    /**
     * 游戏列表
     *
     * @param pageValidate   分页参数
     * @param searchValidate 搜索参数
     * @return PageResult<ArticleListVo>
     * @author fzr
     */
    PageResult<ArticleListVo> list(PageValidate pageValidate, ArticleSearchValidate searchValidate);

    /**
     * 资源详情
     *
     * @param id 主键ID
     * @author fzr
     */
    ArticleDetailVo detail(String gid);
    
     /**
     * 编辑保存
     *
     * @param updateValidate 参数
     * @author fzr
     */
    void save(ArticleValidate saveValidate);

    PageResult<ComicItem> getItems(PageValidate pageValidate, String comicId, Integer chapterNo);

    void delete(String comicId);
}
