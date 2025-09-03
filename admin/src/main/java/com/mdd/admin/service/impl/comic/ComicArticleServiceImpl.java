package com.mdd.admin.service.impl.comic;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.admin.service.comic.IComicArticleService;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.comic.ArticleValidate;
import com.mdd.admin.validate.comic.ArticleSearchValidate;
import com.mdd.admin.vo.comic.SummaryVo;
import com.mdd.admin.vo.comic.ArticleListVo;
import com.mdd.admin.vo.comic.ArticleDetailVo;
import com.mdd.common.core.PageResult;
import com.mdd.common.exception.OperateException;
import com.mdd.common.mapper.comic.ComicArticleMapper;
import com.mdd.common.mapper.comic.ComicTagMapper;
import com.mdd.common.mapper.comic.ChapterMapper;
import com.mdd.common.mapper.comic.TagArticleMapper;
import com.mdd.common.mapper.comic.ComicItemMapper;
import com.mdd.common.entity.comic.ComicArticle;
import com.mdd.common.entity.comic.Chapter;
import com.mdd.common.entity.comic.ComicTag;
import com.mdd.common.entity.comic.ArticleTag;
import com.mdd.common.entity.comic.ComicItem;
import lombok.extern.slf4j.Slf4j;
import com.mdd.common.util.PinyinUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import javax.annotation.Resource;
import com.mdd.common.util.YmlUtils;
import java.util.*;
/**
 * Comic Article 服务实现类
 */
@Slf4j
@Service
public class ComicArticleServiceImpl implements IComicArticleService {

    @Resource
    @Autowired
    ComicArticleMapper comicArticleMapper;

    @Autowired
    ChapterMapper chapterMapper;

    @Autowired
    ComicTagMapper comicTagMapper;

    @Autowired
    TagArticleMapper tagArticleMapper;

    @Autowired
    ComicItemMapper comicItemMapper;

    /**
     * 所有
     *
     * @author fzr
     * @return List<SummaryVo>
     */
    @Override
    public List<SummaryVo> all(String status) {
        List<ComicArticle> listArticle;
        if (!status.isEmpty()) {
            listArticle = comicArticleMapper.selectList(new QueryWrapper<ComicArticle>()
                    .eq("status", status));
        } else {
            listArticle = comicArticleMapper.selectList(new QueryWrapper<ComicArticle>().ne("status", "offline"));
        }

        List<SummaryVo> listResult = new ArrayList<>();
        for (ComicArticle comic : listArticle) {
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
        MPJQueryWrapper<ComicArticle> mpjQueryWrapper = new MPJQueryWrapper<ComicArticle>()
                .select("t.id as id,"
                        + "t.comic_id as comicId,"
                        + "c.title as category, "
                        + "t.title as title, "
                        + "t.author as author, "
                        + "t.state as state, "
                        + "t.type as type, "
                        + "t.cover_image as cover_image, "
                        + "t.updated_at as updated_at, "
                        + "t.status as status,"
                        + "COUNT(p.comic_id) as chapter_count")
                .innerJoin("comic_category_article ca ON t.comic_id=ca.comic_id")
                .innerJoin("comic_category c ON ca.category_id=c.category_id")
                .innerJoin("comic_chapter p ON t.comic_id=p.comic_id")
                .groupBy("t.comic_id, c.title, t.title, t.author, t.state, t.cover_image, t.updated_at, t.status")
                .orderByDesc("t.updated_at");
        String text = searchValidate.getText();
        if (text != null && !text.isEmpty()) {
            mpjQueryWrapper.nested(wq -> wq.like("t.title", text).or().like("t.author", text)
                    .or().like("t.descript", text));
        }
        if (searchValidate.getStatus() != null && !searchValidate.getStatus().isEmpty()) {
            mpjQueryWrapper.nested(wq -> wq.eq("t.status", searchValidate.getStatus()));
        }
        if (searchValidate.getType() != null && !searchValidate.getType().isEmpty()) {
            mpjQueryWrapper.nested(wq -> wq.eq("t.type", searchValidate.getType()));
        }
        IPage<ArticleListVo> iPage = comicArticleMapper.selectJoinPage(
                new Page<>(pageNo, pageSize),
                ArticleListVo.class,
                mpjQueryWrapper);
        iPage.getRecords().forEach(item -> {
            item.setCoverImage(YmlUtils.get("app.cdn") + item.getCoverImage());
        });
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
        ComicArticle model = comicArticleMapper.selectOne(
                new QueryWrapper<ComicArticle>()
                        .eq("comic_id", comicId));
        Assert.notNull(model, "漫画不存在");
        List<Chapter> lsChapter = chapterMapper.selectList(new QueryWrapper<Chapter>().eq("comic_id", comicId));
        ArticleDetailVo vo = new ArticleDetailVo();
        BeanUtils.copyProperties(model, vo);
        vo.setChapters(lsChapter);
        vo.setCoverImage(YmlUtils.get("app.cdn") + vo.getCoverImage());
        return vo;
    }

    @Override
    @Transactional
    public void save(ArticleValidate saveValidate) {
        ComicArticle model;
        Boolean isNew = false;
        String comicId = saveValidate.getComicId();
        if (comicId == null || comicId.isEmpty()) {
            comicId = "x_" + comicArticleMapper.findMaxId() + 1;
        }
        model = comicArticleMapper.selectOne(
                new QueryWrapper<ComicArticle>()
                        .eq("comic_id", comicId));
        if (model == null) {
            model = new ComicArticle();
            saveValidate.setComicId(comicId);
            model.setComicId(comicId);
            model.setCreatedAt(System.currentTimeMillis() / 1000);
            isNew = true;
        }
        BeanUtils.copyProperties(saveValidate, model);
        model.setUpdatedAt(System.currentTimeMillis() / 1000);
        Integer modelId = 0;
        if (isNew) {
            modelId = comicArticleMapper.insert(model);
        } else {
            modelId = comicArticleMapper.updateById(model);
        }
        if (modelId < 1) {
            throw new OperateException("保存失败");
        }
        // 章节处理
        // 先清理所有原来的章节
        String finalComicId = model.getComicId();
        chapterMapper.delete(new QueryWrapper<Chapter>().eq("comic_id", finalComicId));
        saveValidate.getChapters().forEach(chapter -> {
            Chapter chapterModel = new Chapter();
            BeanUtils.copyProperties(chapter, chapterModel);
            chapterModel.setComicId(finalComicId);
            chapterMapper.insert(chapterModel);
        });
        //
        commitComicTag(finalComicId, saveValidate.getTags());
    }

    @Override
    public void delete(String comicId) {
        ComicArticle model = comicArticleMapper.selectOne(
                new QueryWrapper<ComicArticle>()
                        .eq("comic_id", comicId)
                        .ne("status", "offline"));
        Assert.notNull(model, "漫画不存在");
        model.setStatus("offline");
        comicArticleMapper.updateById(model);
    }

    private void commitComicTag(String comicId, List<String> tags) {
        for (String tag : tags) {
            String tagId = PinyinUtil.convertToPinyin(tag);
            ComicTag comicTag = comicTagMapper.selectOne(
                    new QueryWrapper<ComicTag>()
                            .eq("tag_id", tagId));
            if (comicTag == null) {
                comicTag = new ComicTag();
                comicTag.setTagId(tagId);
                comicTag.setTitle(tag);
                comicTagMapper.insert(comicTag);
            }
            tagArticleMapper.delete(new QueryWrapper<ArticleTag>()
                    .eq("tag_id", tagId)
                    .eq("comic_id", comicId));
            ArticleTag articleTag = new ArticleTag();
            articleTag.setTagId(tagId);
            articleTag.setComicId(comicId);
            tagArticleMapper.insert(articleTag);
        }

    }

    @Override
    public PageResult<ComicItem> getItems(PageValidate pageValidate, String comicId, Integer chapterNo){
         Integer pageNo = pageValidate.getPage_no();
        Integer pageSize = pageValidate.getPage_size();
        IPage<ComicItem> iPage = comicItemMapper.selectPage(
                new Page<>(pageNo, pageSize),
                new QueryWrapper<ComicItem>()
                        .eq("comic_id", comicId)
                        .eq("chapter_no", chapterNo));
        iPage.getRecords().forEach(item -> {
            item.setUri(YmlUtils.get("app.cdn") + item.getUri());
        });
        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), iPage.getRecords());
    }
}
