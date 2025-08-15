package com.mdd.common.mapper.xmod;

import com.mdd.common.entity.game.GameInfo;
import com.mdd.common.core.basics.IBaseMapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * MOD 映射器
 */
@Mapper
public interface GameMapper extends IBaseMapper<GameInfo> {
}
