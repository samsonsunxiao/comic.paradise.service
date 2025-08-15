package com.mdd.admin.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.mdd.admin.service.IDecorateDataService;
import com.mdd.admin.vo.decorate.DecorateDataArticleVo;
import com.mdd.admin.vo.decorate.DecoratePageVo;
import com.mdd.common.entity.article.Article;
import com.mdd.common.entity.decorate.DecoratePage;
import com.mdd.common.mapper.article.ArticleMapper;
import com.mdd.common.mapper.decorate.DecoratePageMapper;
import com.mdd.common.util.StringUtils;
import com.mdd.common.util.TimeUtils;
import com.mdd.common.util.UrlUtils;
import com.mdd.common.util.YmlUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
 * 装修数据服务实现类
 */
@Service
public class DecorateDataServiceImpl implements IDecorateDataService {

    @Resource
    ArticleMapper articleMapper;

    @Resource
    DecoratePageMapper decoratePageMapper;

    /**
     * 获取文章数据
     *
     * @author fzr
     * @param limit 条数
     * @return List<DecorateArticleDataVo>
     */
    @Override
    public List<DecorateDataArticleVo> article(Integer limit) {

        List<Article> articles = articleMapper.selectList(new QueryWrapper<Article>()
                .eq("is_show", 1)
                .orderByDesc("id")
                .last("limit " + limit));

        List<DecorateDataArticleVo> list = new LinkedList<>();
        for (Article article : articles) {
            DecorateDataArticleVo vo = new DecorateDataArticleVo();
            BeanUtils.copyProperties(article, vo);
            vo.setImage(UrlUtils.toAdminAbsoluteUrl(vo.getImage()));

            Integer clickActual = vo.getClickActual() != null ? vo.getClickActual() : 0;

            Integer clickVirtual = vo.getClickVirtual() != null ? vo.getClickVirtual() : 0;

            Integer click = clickActual + clickVirtual;

            vo.setClick(click);

            vo.setCreateTime(TimeUtils.timestampToDate(vo.getCreateTime()));
            list.add(vo);
        }

        return list;
    }

    @Override
    public JSONObject pc() {

        JSONObject pc = new JSONObject();
        DecoratePage decoratePage = decoratePageMapper.selectById(4);
        Assert.notNull(decoratePage, "数据不存在!");


        String frontUrl = YmlUtils.get("like.front-url");

        frontUrl = StringUtils.isNotEmpty(frontUrl) && frontUrl.endsWith("/") ? frontUrl + "pc" : frontUrl + "/pc";

        pc.put("update_time", TimeUtils.timestampToDate(decoratePage.getUpdateTime()));
        pc.put("pc_url",  frontUrl);
        return pc;
    }
}
