package com.mdd.front.service;

import com.mdd.common.entity.decorate.DecorateTabbar;
import com.mdd.front.vo.decorateTabbar.DecorateTabbarVo;

import java.util.List;

/**
 * 底部导航服务接口类
 */
public interface IDecorateTabbarService {

    /**
     * @notes 获取底部导航列表
     * @return array
     * @author damonyuan
     */
    List<DecorateTabbarVo> getTabbarLists();

}
