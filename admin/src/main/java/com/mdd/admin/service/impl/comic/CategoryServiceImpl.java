package com.mdd.admin.service.impl.comic;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.service.comic.ICategoryService;
import com.mdd.admin.validate.comic.CategoryValidate;
import com.mdd.common.core.PageResult;
import com.mdd.admin.vo.comic.CategoryDetailVo;
import com.mdd.admin.vo.comic.CategoryListVo;
import com.mdd.admin.vo.comic.SummaryVo;
import com.mdd.common.entity.comic.ComicArticle;
import com.mdd.common.entity.comic.Category;
import com.mdd.common.entity.comic.CategoryArticle;
import com.mdd.common.mapper.comic.CategoryMapper;
import com.mdd.common.util.PinyinUtil;
import com.mdd.common.mapper.comic.ComicArticleMapper;
import com.mdd.common.mapper.comic.CategoryArticleMapper;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import javax.annotation.Resource;

import java.util.*;
import java.util.stream.Collectors;

/**
 * category服务实现类
 */
@Slf4j
@Service
public class CategoryServiceImpl implements ICategoryService {

    @Resource
    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    ComicArticleMapper comicArticleMapper;

    @Autowired
    CategoryArticleMapper categoryArticleMapper;

    /**
     * 所有
     *
     * @author fzr
     * @return List<GameNameVo>
     */
    @Override
    public List<Category> all() {
        List<Category> listModule = categoryMapper.selectList(new QueryWrapper<Category>());
        return listModule;
    }

    /**
     * 列表
     *
     * @param pageValidate   分页参数
     * @param searchValidate 搜索参数
     * @return PageResult<ResourceListedVo>
     * @author fzr
     */
    @Override
    public PageResult<CategoryListVo> list(PageValidate pageValidate) {
        Integer pageNo = pageValidate.getPage_no();
        Integer pageSize = pageValidate.getPage_size();
        MPJQueryWrapper<Category> mpjQueryWrapper = new MPJQueryWrapper<Category>()
                .selectAll(Category.class)
                .orderByDesc("t.id");
        IPage<Category> iPage = categoryMapper.selectJoinPage(
                new Page<>(pageNo, pageSize),
                Category.class,
                mpjQueryWrapper);
        List<CategoryListVo> listCategoies = new ArrayList<>();
        for (Category item : iPage.getRecords()) {
            CategoryListVo vo = new CategoryListVo();
            BeanUtils.copyProperties(item, vo);
            Long count = comicArticleMapper
                    .selectCount(new QueryWrapper<ComicArticle>().eq("category_id", item.getCategoryId()));
            if (count != null) {
                vo.setCount(count);
            }
            listCategoies.add(vo);
        }
        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), listCategoies);
    }

    /**
     * 资源详情
     *
     * @param id 主键ID
     * @author fzr
     */
    @Override
    public CategoryDetailVo detail(String category_id) {
        Category model = categoryMapper.selectOne(
                new QueryWrapper<Category>()
                        .eq("category_id", category_id));
        Assert.notNull(model, "分类不存在");
        MPJQueryWrapper<ComicArticle> mpjQueryWrapper = new MPJQueryWrapper<ComicArticle>()
                .selectAll(ComicArticle.class)
                .innerJoin("comic_category_article ca ON t.comic_id=ca.comic_id")
                .innerJoin("comic_category c ON c.category_id=ca.category_id")
                .eq("ca.category_id", category_id);
        IPage<SummaryVo> iPage = comicArticleMapper.selectJoinPage(
                new Page<>(0, -1),
                SummaryVo.class,
                mpjQueryWrapper);
        CategoryDetailVo moduleDetailVo = new CategoryDetailVo();
        BeanUtils.copyProperties(model, moduleDetailVo);
        moduleDetailVo.setComics(iPage.getRecords());
        return moduleDetailVo;
    }

    @Override
    public void commitBatchComics(CategoryValidate saveValidate) {
        Boolean isNew = false;
        String categoryId = PinyinUtil.convertToPinyin(saveValidate.getTitle());
        Category model = categoryMapper.selectOne(
                    new QueryWrapper<Category>()
                            .eq("category_id", categoryId));
        if (model == null) {
            isNew = true;
            model = new Category();
            model.setCategoryId(categoryId);
        } else {
            Assert.notNull(model, "分类不存在");                
        }
        model.setTitle(saveValidate.getTitle());
        if (isNew) {
            categoryMapper.insert(model);
        } else {
            categoryMapper.updateById(model);
        }
        List<CategoryArticle> listCategoryArticle = categoryArticleMapper
                .selectList(new QueryWrapper<CategoryArticle>().eq("category_id", categoryId));
        List<String> comicIdList = listCategoryArticle.stream().map(CategoryArticle::getComicId).collect(Collectors.toList());
        List<String> listAdd = saveValidate.getComics().stream()
                .filter(comicId1 -> comicIdList.stream().noneMatch(comicId2 -> comicId1.equals(comicId2))).collect(Collectors.toList());
        for (String comicid : listAdd) {
            CategoryArticle categoryArticle = new CategoryArticle();
            categoryArticle.setComicId(comicid);
            categoryArticle.setCategoryId(model.getCategoryId());
            categoryArticleMapper.insert(categoryArticle);
        }
        List<String> listDel = comicIdList.stream()
                .filter(comicId1 -> saveValidate.getComics().stream().noneMatch(comicId2 -> comicId1.equals(comicId2)))
                .collect(Collectors.toList());
        for (String comicid : listDel) {
            categoryArticleMapper.delete(
                    new QueryWrapper<CategoryArticle>().eq("comic_id", comicid).eq("category_id", categoryId));
        }
    }

    @Override
    public void commitSingleComic(CategoryValidate saveValidate, String comicId)
    {
        Boolean isNew = false;
        String categoryId = PinyinUtil.convertToPinyin(saveValidate.getTitle());
        Category model = categoryMapper.selectOne(
                    new QueryWrapper<Category>()
                            .eq("category_id", categoryId));
        if (model == null) {
            isNew = true;
            model = new Category();
            model.setCategoryId(categoryId);
        } else {
            Assert.notNull(model, "分类不存在");                
        }
        model.setTitle(saveValidate.getTitle());
        if (isNew) {
            categoryMapper.insert(model);
        } else {
            categoryMapper.updateById(model);
        }
        CategoryArticle categoryArticle = categoryArticleMapper.selectOne(
                new QueryWrapper<CategoryArticle>()
                        .eq("comic_id", comicId)
                        .eq("category_id", categoryId));
        if (categoryArticle == null) {
            categoryArticle = new CategoryArticle();
            categoryArticle.setComicId(comicId);
            categoryArticle.setCategoryId(categoryId);
            categoryArticleMapper.insert(categoryArticle);
        }
    }

    public void del(Integer id) {
        // 先删除有关系的游戏
        Category model = categoryMapper.selectOne(
                new QueryWrapper<Category>()
                        .eq("id", id));
        Assert.notNull(model, "模块不存在");
        categoryArticleMapper.delete(new QueryWrapper<CategoryArticle>().eq("category_id", model.getCategoryId()));
        categoryMapper.deleteById(id);
    }
}
