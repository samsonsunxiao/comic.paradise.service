package com.mdd.admin.service.impl.comic;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.admin.service.comic.IArticleService;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.comic.ArticleValidate;
import com.mdd.admin.validate.comic.ArticleSearchValidate;
import com.mdd.admin.vo.comic.SummaryVo;
import com.mdd.admin.vo.comic.ArticleListVo;
import com.mdd.admin.vo.comic.ArticleDetailVo;
import com.mdd.common.core.PageResult;
import com.mdd.common.exception.OperateException;
import com.mdd.common.mapper.comic.ArticleMapper;
import com.mdd.common.mapper.comic.CategoryArticleMapper;
import com.mdd.common.mapper.comic.ChapterMapper;
import com.mdd.common.entity.comic.Article;
import com.mdd.common.entity.comic.Chapter;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import javax.annotation.Resource;

import java.util.*;
/**
 * Comic Article 服务实现类
 */
@Slf4j
@Service
public class ArticleServiceImpl implements IArticleService {
    
    @Resource
    @Autowired
    ArticleMapper articleMapper;
    
    @Autowired
    CategoryArticleMapper categoryArticleMapper;
    
     @Autowired
    ChapterMapper chapterMapper;
    
    /**
     * 所有
     *
     * @author fzr
     * @return List<SummaryVo>
     */
    @Override
    public List<SummaryVo> all(String status) {
        List<Article> listArticle;
        if (!status.isEmpty()){
            listArticle = articleMapper.selectList(new QueryWrapper<Article>()
            .eq("status",status));
        }else{
            listArticle = articleMapper.selectList(new QueryWrapper<Article>().ne("status", "offline"));
        }
        
        List<SummaryVo> listResult = new ArrayList<>();
        for (Article comic : listArticle) {
            SummaryVo vo = new SummaryVo();
            BeanUtils.copyProperties(comic, vo);  
            listResult.add(vo);
        }  
        return listResult;
    }
    /**
     * 漫画列表
     *
     * @param pageValidate   分页参数
     * @param searchValidate 搜索参数
     * @return PageResult<ArticleListVo>
     * @author fzr
     */
    @Override
    public PageResult<ArticleListVo> list(PageValidate pageValidate, ArticleSearchValidate searchValidate) {
        Integer pageNo = pageValidate.getPage_no();
        Integer pageSize = pageValidate.getPage_size();
        MPJQueryWrapper<Article> mpjQueryWrapper = new MPJQueryWrapper<Article>()
                .select("t.id as id,"
                        +"c.title as category, "
                        +"t.title as title, "
                        +"t.author as author, "
                        +"t.state as state, "
                        +"t.cover_image as cover_image, "
                        +"t.updated_at as updated_at, "
                        +"t.status as status," 
                        +"COUNT(p.comic_id) as count")
                .innerJoin("comic_category_article c ON t.comic_id=c.comic_id")
                .innerJoin("comic_chapter p ON t.comic_id=p.comic_id")
                .orderByDesc("t.updated_at");
        String text = searchValidate.getText();
        if (text != null && !text.isEmpty()){
            mpjQueryWrapper.nested(wq -> wq.like("t.title", text).or().like("t.author", text)
                    .or().like("t.descript", text));
        }
        if (searchValidate.getStatus() != null && !searchValidate.getStatus().isEmpty())         {
            mpjQueryWrapper.nested(wq->wq.eq("t.status", searchValidate.getStatus()));  
        }
        if (searchValidate.getType() != null && !searchValidate.getType().isEmpty()){
            mpjQueryWrapper.nested(wq->wq.eq("t.type", searchValidate.getType()));  
        }
        IPage<ArticleListVo> iPage = articleMapper.selectJoinPage(
                new Page<>(pageNo, pageSize),
                ArticleListVo.class,
                mpjQueryWrapper);
        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), iPage.getRecords());
    }

    /**
     * 资源详情
     *
     * @param id 主键ID
     * @author fzr
     */
    @Override
    public ArticleDetailVo detail(String comicId) {
        Article model = articleMapper.selectOne(
                new QueryWrapper<Article>()
                        .eq("comic_id", comicId)
        );
        Assert.notNull(model, "漫画不存在");
        List<Chapter> lsChapter = chapterMapper.selectList(new
            QueryWrapper<Chapter>().eq("comic_id", comicId));
        ArticleDetailVo vo = new ArticleDetailVo();
        BeanUtils.copyProperties(model, vo);
        vo.setChapters(lsChapter);
        return vo;
    }

    @Override
    @Transactional
    public void save(ArticleValidate saveValidate)
    {
        Article model;
        Boolean isNew = false;
        if (saveValidate.getComicId() == null || saveValidate.getComicId().isEmpty()) {
            model = new Article();
            isNew = true;
        }else{
            model = articleMapper.selectOne(
                new QueryWrapper<Article>()
                        .eq("comic_id", saveValidate.getComicId())
                        .ne("status", "offline"));
            Assert.notNull(model, "漫画不存在");
        }
        BeanUtils.copyProperties(saveValidate, model);
        Integer modelId = 0;
        if (isNew){
            modelId = articleMapper.insert(model);
        }else{
            modelId = articleMapper.updateById(model);
        }
        if (modelId < 1) {
            throw new OperateException("保存失败");
        }
        //章节处理
        //先清理所有原来的章节
        chapterMapper.delete(new QueryWrapper<Chapter>().eq("comic_id", model.getComicId()));
        saveValidate.getChapters().forEach(chapter -> {
            Chapter chapterModel = new Chapter();
            BeanUtils.copyProperties(chapter, chapterModel);
            chapterModel.setComicId(model.getComicId());
            chapterMapper.insert(chapterModel);
        });
    }

    @Override
    public void delete(String comicId){
        Article model = articleMapper.selectOne(
                new QueryWrapper<Article>()
                        .eq("comic_id", comicId)
                        .ne("status", "offline"));
        Assert.notNull(model, "漫画不存在");
        model.setStatus("offline");
        articleMapper.updateById(model);
    }
    
}
