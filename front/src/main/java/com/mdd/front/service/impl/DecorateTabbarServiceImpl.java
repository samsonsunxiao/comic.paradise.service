package com.mdd.front.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mdd.common.entity.decorate.DecorateTabbar;
import com.mdd.common.enums.YesNoEnum;
import com.mdd.common.mapper.decorate.DecorateTabbarMapper;
import com.mdd.common.util.*;
import com.mdd.front.service.IDecorateTabbarService;
import com.mdd.front.vo.decorateTabbar.DecorateTabbarVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 底部导航服务实现类
 */
@Service
public class DecorateTabbarServiceImpl implements IDecorateTabbarService {

    @Resource
    DecorateTabbarMapper decorateTabbarMapper;


    @Override
    public List<DecorateTabbarVo> getTabbarLists() {
        List<DecorateTabbar> list = decorateTabbarMapper.selectList(new QueryWrapper<DecorateTabbar>().eq("is_show", YesNoEnum.YES.getCode()));
        List<DecorateTabbarVo> ret = new ArrayList<DecorateTabbarVo>();
        if (list.size() == 0) {
            return ret;
        }

        for (DecorateTabbar item : list) {
            DecorateTabbarVo vo = new DecorateTabbarVo();
            BeanUtils.copyProperties(item, vo);
            if (StringUtils.isNotEmpty(item.getSelected())) {
                vo.setSelected(UrlUtils.toAbsoluteUrl(item.getSelected()));
            }

            if (StringUtils.isNotEmpty(item.getUnselected())) {
                vo.setUnselected(UrlUtils.toAbsoluteUrl(item.getUnselected()));
            }

            vo.setCreateTime(TimeUtils.timestampToDate(item.getCreateTime()));
            vo.setUpdateTime(TimeUtils.timestampToDate(item.getUpdateTime()));
            vo.setLink(StringUtils.isEmpty(item.getLink()) ? new JSONObject() : JSONObject.parse(item.getLink()));
            ret.add(vo);
        }
        return ret;
    }
}
