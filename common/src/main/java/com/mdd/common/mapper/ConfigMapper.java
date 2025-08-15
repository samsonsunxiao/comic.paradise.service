package com.mdd.common.mapper;

import com.mdd.common.core.basics.IBaseMapper;
import com.mdd.common.entity.Config;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统配置
 */
@Mapper
public interface ConfigMapper extends IBaseMapper<Config> {
}
