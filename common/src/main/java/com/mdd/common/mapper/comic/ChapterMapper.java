package com.mdd.common.mapper.comic;

import com.mdd.common.entity.comic.Chapter;
import com.mdd.common.core.basics.IBaseMapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * 章节 映射器
 */
@Mapper
public interface ChapterMapper extends IBaseMapper<Chapter> {
}
