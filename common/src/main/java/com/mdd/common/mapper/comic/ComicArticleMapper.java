package com.mdd.common.mapper.comic;

import com.mdd.common.entity.comic.ComicArticle;
import com.mdd.common.core.basics.IBaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * MOD 映射器
 */
@Mapper
public interface ComicArticleMapper extends IBaseMapper<ComicArticle> {

    @Select("SELECT MAX(id) FROM comic_article")
    Integer findMaxId();
}
