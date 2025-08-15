package com.mdd.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.admin.service.IArticleService;
import com.mdd.admin.validate.article.ArticleCreateValidate;
import com.mdd.admin.validate.article.ArticleSearchValidate;
import com.mdd.admin.validate.article.ArticleUpdateValidate;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.vo.article.ArticleDetailVo;
import com.mdd.admin.vo.article.ArticleListedVo;
import com.mdd.common.config.GlobalConfig;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.article.Article;
import com.mdd.common.entity.article.ArticleCate;
import com.mdd.common.mapper.article.ArticleCateMapper;
import com.mdd.common.mapper.article.ArticleMapper;
import com.mdd.common.util.StringUtils;
import com.mdd.common.util.TimeUtils;
import com.mdd.common.util.UrlUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * 文章服务实现类
 */
@Service
public class ArticleServiceImpl implements IArticleService {

    @Resource
    ArticleMapper articleMapper;

    @Resource
    ArticleCateMapper articleCategoryMapper;

    /**
     * 文章列表
     *
     * @author fzr
     * @param pageValidate 分页参数
     * @param searchValidate 搜索参数
     * @return PageResult<ArticleListVo>
     */
    @Override
    public PageResult<ArticleListedVo> list(PageValidate pageValidate, ArticleSearchValidate searchValidate) {
        Integer pageNo   = pageValidate.getPage_no();
        Integer pageSize = pageValidate.getPage_size();

        MPJQueryWrapper<Article> mpjQueryWrapper = new MPJQueryWrapper<Article>()
                .selectAll(Article.class)
                .select("ac.name as cateName")
                .innerJoin("?_article_cate ac ON ac.id=t.cid".replace("?_", GlobalConfig.tablePrefix))
                .isNull("t.delete_time")
                .orderByDesc(Arrays.asList("t.sort", "t.id"));

        articleMapper.setSearch(mpjQueryWrapper, searchValidate, new String[]{
                "like:title@t.title:str",
                "=:cid@t.cid:int",
                "=:is_show@t.is_show:int",
                "datetime:startTime-endTime@t.create_time:str"
        });

        IPage<ArticleListedVo> iPage = articleMapper.selectJoinPage(
                new Page<>(pageNo, pageSize),
                ArticleListedVo.class,
                mpjQueryWrapper);

        for (ArticleListedVo vo : iPage.getRecords()) {
            vo.setImage(UrlUtils.toAdminAbsoluteUrl(vo.getImage()));

            Integer clickActual = vo.getClickActual() != null ? vo.getClickActual() : 0;

            Integer clickVirtual = vo.getClickVirtual() != null ? vo.getClickVirtual() : 0;

            Integer click = clickActual + clickVirtual;

            vo.setClick(click);

            vo.setCreateTime(TimeUtils.timestampToDate(vo.getCreateTime()));
            vo.setUpdateTime(TimeUtils.timestampToDate(vo.getUpdateTime()));
        }

        return PageResult.iPageHandle(iPage);
    }

    /**
     * 文章详情
     *
     * @author fzr
     * @param id 主键ID
     */
    @Override
    public ArticleDetailVo detail(Integer id) {
        Article model = articleMapper.selectOne(
                new QueryWrapper<Article>()
                        .select(Article.class, info->
                          !info.getColumn().equals("delete_time"))
                        .eq("id", id)
                        .isNull("delete_time"));

        Assert.notNull(model, "文章不存在");

        ArticleDetailVo vo = new ArticleDetailVo();
        BeanUtils.copyProperties(model, vo);
        vo.setContent(StringUtils.isNull(model.getContent()) ? "" : model.getContent());
        vo.setImage(UrlUtils.toAdminAbsoluteUrl(model.getImage()));
        vo.setCreateTime(TimeUtils.timestampToDate(model.getCreateTime()));
        vo.setUpdateTime(TimeUtils.timestampToDate(model.getUpdateTime()));

        return vo;
    }

    /**
     * 文章新增
     *
     * @author fzr
     * @param createValidate 文章参数
     */
    @Override
    public void add(ArticleCreateValidate createValidate) {
        Article model = new Article();
        model.setCid(createValidate.getCid());
        model.setTitle(createValidate.getTitle());
        model.setImage(UrlUtils.toRelativeUrl(createValidate.getImage()));
        model.setContent(createValidate.getContent());
        model.setSort(createValidate.getSort());
        model.setIsShow(createValidate.getIsShow());
        model.setAuthor(createValidate.getAuthor());
        model.setAbstractField(createValidate.getAbstractFied());
        model.setDesc(createValidate.getDesc());

        model.setCreateTime(TimeUtils.timestamp());
        model.setUpdateTime(TimeUtils.timestamp());
        model.setClickVirtual(createValidate.getClick_virtual());
        articleMapper.insert(model);
    }

    /**
     * 文章编辑
     *
     * @author fzr
     * @param updateValidate 文章参数
     */
    @Override
    public void edit(ArticleUpdateValidate updateValidate) {
        Article model = articleMapper.selectOne(
                new QueryWrapper<Article>()
                .eq("id", updateValidate.getId())
                .isNull("delete_time"));

        Assert.notNull(model, "文章不存在!");

        Assert.notNull(articleCategoryMapper.selectOne(
                new QueryWrapper<ArticleCate>()
                .eq("id", updateValidate.getCid())
                .isNull("delete_time")), "分类不存在");

        model.setCid(updateValidate.getCid());
        model.setTitle(updateValidate.getTitle());
        model.setImage(UrlUtils.toRelativeUrl(updateValidate.getImage()));
        model.setContent(updateValidate.getContent());
        model.setIsShow(updateValidate.getIsShow());
        model.setAuthor(updateValidate.getAuthor());
        model.setSort(updateValidate.getSort());
        model.setAbstractField(updateValidate.getAbstractFied());
        model.setDesc(updateValidate.getDesc());
        model.setUpdateTime(TimeUtils.timestamp());
        model.setClickVirtual(updateValidate.getClick_virtual());
        articleMapper.updateById(model);
    }

    /**
     * 文章删除
     *
     * @author fzr
     * @param id 文章ID
     */
    @Override
    public void del(Integer id) {
        Article article = articleMapper.selectOne(
                new QueryWrapper<Article>()
                        .select("id, is_show")
                        .eq("id", id)
                        .isNull("delete_time"));

        Assert.notNull(article, "文章不存在!");
        article.setDeleteTime(TimeUtils.timestamp());
        articleMapper.updateById(article);
    }

    /**
     * 文章状态
     *
     * @author fzr
     * @param id 文章主键
     */
    @Override
    public void change(Integer id) {
        Article article = articleMapper.selectOne(
                new QueryWrapper<Article>()
                        .select("id, is_show")
                        .eq("id", id)
                        .isNull("delete_time"));

        Assert.notNull(article, "文章不存在!");

        article.setIsShow(article.getIsShow() == 0 ? 1:0);
        article.setUpdateTime(TimeUtils.timestamp());
        articleMapper.updateById(article);
    }

}
