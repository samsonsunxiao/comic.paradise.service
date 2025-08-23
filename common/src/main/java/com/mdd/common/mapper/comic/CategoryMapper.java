package com.mdd.common.mapper.comic;

import com.mdd.common.core.basics.IBaseMapper;
import com.mdd.common.entity.comic.Category;

import org.apache.ibatis.annotations.Mapper;

/**
 * MOD 映射器
 */
@Mapper
public interface CategoryMapper extends IBaseMapper<Category> {
}
