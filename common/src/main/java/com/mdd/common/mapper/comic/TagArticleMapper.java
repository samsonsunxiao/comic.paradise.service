package com.mdd.common.mapper.comic;

import com.mdd.common.core.basics.IBaseMapper;
import com.mdd.common.entity.comic.ArticleTag;

import org.apache.ibatis.annotations.Mapper;

/**
 * MOD 映射器
 */
@Mapper
public interface TagArticleMapper extends IBaseMapper<ArticleTag> {
}
