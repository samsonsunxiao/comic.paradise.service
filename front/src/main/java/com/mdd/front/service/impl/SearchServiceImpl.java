package com.mdd.front.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.mdd.common.config.GlobalConfig;
import com.mdd.common.entity.article.Article;
import com.mdd.common.entity.decorate.DecoratePage;
import com.mdd.common.entity.setting.HotSearch;
import com.mdd.common.mapper.article.ArticleMapper;
import com.mdd.common.mapper.decorate.DecoratePageMapper;
import com.mdd.common.mapper.decorate.DecorateTabbarMapper;
import com.mdd.common.mapper.setting.HotSearchMapper;
import com.mdd.common.util.*;
import com.mdd.front.service.IDecorateTabbarService;
import com.mdd.front.service.IIndexService;
import com.mdd.front.service.ISearchService;
import com.mdd.front.vo.decorateTabbar.DecorateTabbarVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 首页服务实现类
 */
@Service
public class SearchServiceImpl implements ISearchService {


    @Resource
    HotSearchMapper hotSearchMapper;

    @Override
    public JSONObject hotLists() {

        List<HotSearch> hotSearches = hotSearchMapper.selectList(new QueryWrapper<HotSearch>().orderByDesc("sort"));
        JSONObject result = new JSONObject(){{
            put("status", ConfigUtils.get("hot_search", "status", "0"));
            put("data", hotSearches);
        }};

        return result;
    }
}
