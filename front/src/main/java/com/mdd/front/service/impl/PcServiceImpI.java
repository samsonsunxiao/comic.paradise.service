package com.mdd.front.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mdd.common.config.GlobalConfig;
import com.mdd.common.entity.article.ArticleCate;
import com.mdd.common.entity.decorate.DecoratePage;
import com.mdd.common.entity.article.Article;
import com.mdd.common.entity.article.ArticleCollect;
import com.mdd.common.enums.YesNoEnum;
import com.mdd.common.exception.OperateException;
import com.mdd.common.mapper.article.ArticleCateMapper;
import com.mdd.common.mapper.decorate.DecoratePageMapper;
import com.mdd.common.mapper.article.ArticleCollectMapper;
import com.mdd.common.mapper.article.ArticleMapper;
import com.mdd.common.util.*;
import com.mdd.front.service.IPcService;
import com.mdd.front.validate.OcpcValidate;
import com.mdd.front.vo.article.PcArticleCenterVo;
import com.mdd.front.vo.article.PcArticleDetailVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class PcServiceImpI implements IPcService {

    @Resource
    DecoratePageMapper decoratePageMapper;

    @Resource
    ArticleCateMapper articleCategoryMapper;

    @Resource
    ArticleCollectMapper articleCollectMapper;

    @Resource
    ArticleMapper articleMapper;

    /**
     * 主页
     *
     * @author cjh
     * @return Map<String, Object>
     */
    @Override
    public Map<String, Object> index() {
        Map<String,Object> indexData = new LinkedHashMap<>();
        DecoratePage decoratePage = decoratePageMapper.selectOne(
                new QueryWrapper<DecoratePage>()
                        .eq("id", 4)
                        .last("limit 1"));
        //全部资讯
        List<Article> articlesAll = articleMapper.selectList(new QueryWrapper<Article>()
                .eq("is_show", 1)
                .isNull("delete_time")
                .orderByDesc("sort")
                .orderByDesc("id")
                .last("limit 5"));
        List<Map<String, Object>> articlesAllList = new LinkedList<>();
        for (Article article : articlesAll) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", article.getId());
            map.put("cid", article.getCid());
            map.put("author", article.getAuthor());
            map.put("title", article.getTitle());
            map.put("abstract", article.getAbstractField());
            map.put("click", article.getClickActual() + article.getClickVirtual());
            map.put("create_time", TimeUtils.timestampToDate(article.getCreateTime()));
            map.put("desc", article.getDesc());
            map.put("image", UrlUtils.toAbsoluteUrl(article.getImage()));
            articlesAllList.add(map);
        }

        //最新资讯
        List<Article> articlesNew = articleMapper.selectList(new QueryWrapper<Article>()
                .eq("is_show", 1)
                .isNull("delete_time")
                .orderByDesc("id")
                .last("limit 7"));
        List<Map<String, Object>> articlesNewList = new LinkedList<>();
        for (Article article : articlesNew) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", article.getId());
            map.put("cid", article.getCid());
            map.put("author", article.getAuthor());
            map.put("title", article.getTitle());
            map.put("abstract", article.getAbstractField());
            map.put("click", article.getClickActual() + article.getClickVirtual());
            map.put("create_time", TimeUtils.timestampToDate(article.getCreateTime()));
            map.put("desc", article.getDesc());
            map.put("image", UrlUtils.toAbsoluteUrl(article.getImage()));
            articlesNewList.add(map);
        }

        //热门资讯
        List<Article> articlesHot = articleMapper.selectList(new QueryWrapper<Article>()
                .eq("is_show", 1)
                .isNull("delete_time")
                .orderByDesc("click_actual")
                .last("limit 7"));
        List<Map<String, Object>> articlesHostList = new LinkedList<>();
        for (Article article : articlesHot) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", article.getId());
            map.put("cid", article.getCid());
            map.put("author", article.getAuthor());
            map.put("title", article.getTitle());
            map.put("abstract", article.getAbstractField());
            map.put("click", article.getClickActual() + article.getClickVirtual());
            map.put("create_time", TimeUtils.timestampToDate(article.getCreateTime()));
            map.put("desc", article.getDesc());
            map.put("image", UrlUtils.toAbsoluteUrl(article.getImage()));
            articlesHostList.add(map);
        }


        String data = decoratePage.getData();
        JSONArray dataArray = JSONArray.parseArray(data);

        for (int i = 0; i < dataArray.size() ; i++) {
            JSONObject dataJson = dataArray.getJSONObject(i);
            JSONArray tempArray = dataJson.getJSONObject("content").getJSONArray("data");
            if (StringUtils.isNull(tempArray)) {

                JSONObject tempJSON = dataJson.getJSONObject("content");
                if (StringUtils.isNull(tempJSON)) {
                    continue;
                } else {
                    if (StringUtils.isNotNull(tempJSON.getString("image"))) {
                        tempJSON.put("image", UrlUtils.toAbsoluteUrl(tempJSON.getString("image")));
                    }
                    if (StringUtils.isNotNull(tempJSON.getString("qrcode"))) {
                        tempJSON.put("qrcode", UrlUtils.toAbsoluteUrl(tempJSON.getString("qrcode")));
                    }
                }
            } else {
                for (int j = 0; j < tempArray.size(); j++) {
                    JSONObject tempImageJson = tempArray.getJSONObject(j);
                    tempImageJson.put("image", UrlUtils.toAbsoluteUrl(tempImageJson.getString("image")));
                }
            }
        }

        decoratePage.setData(dataArray.toJSONString());

        indexData.put("page", decoratePage);
        indexData.put("all", articlesAllList);
        indexData.put("new", articlesNewList);
        indexData.put("hot", articlesHostList);
        return  indexData;
    }

    /**
     * 配置
     *
     * @author cjh
     * @return Map<String, Object>
     */
    @Override
    public Map<String, Object> getConfig() {
        Map<String, Object> response = new LinkedHashMap<>();

        // 登录配置
        Map<String, Object> loginMap = new LinkedHashMap<>();
        Map<String, String> loginConfig = ConfigUtils.get("login");
        // 登录方式
        loginMap.put("login_way", JSONArray.parseArray(loginConfig.getOrDefault("login_way", "[]")));
        // 注册强制绑定手机
        loginMap.put("coerce_mobile", Integer.parseInt(loginConfig.getOrDefault("coerce_mobile", "0")));
        // 政策协议
        loginMap.put("login_agreement", Integer.parseInt(loginConfig.getOrDefault("login_agreement", "0")));
        // 第三方登录 开关
        loginMap.put("third_auth", Integer.parseInt(loginConfig.getOrDefault("third_auth", "0")));
        // 微信授权登录
        loginMap.put("wechat_auth", Integer.parseInt(loginConfig.getOrDefault("wechat_auth", "0")));
        // qq授权登录
        loginMap.put("qq_auth", Integer.parseInt(loginConfig.getOrDefault("qq_auth", "0")));

        // 站点统计
        String siteStatistics = ConfigUtils.get("siteStatistics", "clarity_code", "");

        // 备案信息
        Map<String, String> websiteConfig = ConfigUtils.get("copyright");
        String copyright = websiteConfig.getOrDefault("config", "[]");
        List<Map<String, String>> copyrightMap = ListUtils.stringToListAsMapStr(copyright);

        // 公众号二维码
        String oaQrCode = ConfigUtils.get("oa_setting", "qr_code", "");
        oaQrCode = StringUtils.isEmpty(oaQrCode) ? "" : UrlUtils.toAbsoluteUrl(oaQrCode);

        // 小程序二维码
        String mnpQrCode = ConfigUtils.get("mnp_setting", "qr_code", "");
        mnpQrCode = StringUtils.isEmpty(mnpQrCode) ? "" : UrlUtils.toAbsoluteUrl(mnpQrCode);

        JSONObject qrcode = new JSONObject();
        qrcode.put("oa", oaQrCode);
        qrcode.put("mnp", mnpQrCode);

        // 网站信息
        JSONObject website = new JSONObject();
        website.put("shop_name", ConfigUtils.get("website", "shop_name", ""));
        website.put("shop_logo", UrlUtils.toAbsoluteUrl(ConfigUtils.get("website", "shop_logo", "")));
        website.put("pc_logo", UrlUtils.toAbsoluteUrl(ConfigUtils.get("website", "pc_logo", "")));
        website.put("pc_title", ConfigUtils.get("website", "pc_title", ""));
        website.put("pc_ico", UrlUtils.toAbsoluteUrl(ConfigUtils.get("website", "pc_ico", "")));
        website.put("pc_desc", ConfigUtils.get("website", "pc_desc", ""));
        website.put("pc_keywords", ConfigUtils.get("website", "pc_keywords", ""));


        response.put("admin_url", "2");
        response.put("copyright", copyrightMap);
        response.put("domain", UrlUtils.domain());
        response.put("login", loginMap);
        response.put("version", GlobalConfig.version);
        response.put("siteStatistics", siteStatistics);
        response.put("website", website);
        response.put("qrcode", qrcode);
        return response;
    }

    /**
     * 资讯中心
     *
     * @author fzr
     * @return List<PcArticleCenterVo>
     */
    @Override
    public List<PcArticleCenterVo> infoCenter() {
        List<ArticleCate> articleCategoryList = articleCategoryMapper.selectList(
                new QueryWrapper<ArticleCate>()
                    .eq("is_show", 1)
                        .isNull("delete_time")
                    .orderByDesc(Arrays.asList("sort", "id")));

        List<PcArticleCenterVo> list = new LinkedList<>();
        for (ArticleCate articleCategory : articleCategoryList) {
            List<Article> articleList = articleMapper.selectList(new QueryWrapper<Article>()
                    .eq("cid", articleCategory.getId())
                    .eq("is_show", 1)
                    .isNull("delete_time")
                    .orderByDesc(Arrays.asList("sort", "id"))
                    .last("limit 10"));

            List<Map<String, Object>> articles = new LinkedList<>();
            for (Article article : articleList) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", article.getId());
                map.put("cid", article.getCid());
                map.put("author", article.getAuthor());
                map.put("title", article.getTitle());
                map.put("abstract", article.getAbstractField());
                map.put("click", article.getClickActual() + article.getClickVirtual());
                map.put("create_time", TimeUtils.timestampToDate(article.getCreateTime()));
                map.put("desc", article.getDesc());
                map.put("image", UrlUtils.toAbsoluteUrl(article.getImage()));
                map.put("is_show", article.getIsShow());
                map.put("sort", article.getSort());
                map.put("update_time", TimeUtils.timestampToDate(article.getUpdateTime()));
                articles.add(map);
            }

            PcArticleCenterVo vo = new PcArticleCenterVo();
            vo.setId(articleCategory.getId());
            vo.setName(articleCategory.getName());
            vo.setArticle(articles);
            list.add(vo);
        }

        return list;
    }

    /**
     * 文章详情
     *
     * @author fzr
     * @param id 文章主键
     * @param userId 用户ID
     * @return PcArticleDetailVo
     */
    @Override
    public PcArticleDetailVo articleDetail(Integer id, Integer userId) {
        // 文章详情
        Article article = articleMapper.selectOne(new QueryWrapper<Article>()
                .eq("id", id)
                .isNull("delete_time")
                .eq("is_show", YesNoEnum.YES.getCode())
                .last("limit 1"));

        if (StringUtils.isNull(article)) {
            throw new OperateException("文章数据不存在!");
        }

        // 分类名称
        ArticleCate articleCategory = articleCategoryMapper.selectOne(
                new QueryWrapper<ArticleCate>()
                    .eq("id", article.getCid())
                        .isNull("delete_time"));

        // 上一条记录
        Article prev = articleMapper.selectOne(new QueryWrapper<Article>()
                .select("id,title")
                .lt("id", id)
                .isNull("delete_time")
                .orderByDesc(Arrays.asList("sort", "id"))
                .last("limit 1"));

        // 下一条记录
        Article next = articleMapper.selectOne(new QueryWrapper<Article>()
                .select("id,title")
                .gt("id", id)
                .isNull("delete_time")
                .orderByDesc(Arrays.asList("sort", "id"))
                .last("limit 1"));

        // 是否收藏
        ArticleCollect collect = articleCollectMapper.selectOne(new QueryWrapper<ArticleCollect>()
                .eq("article_id", article.getId())
                .eq("user_id", userId)
                .eq("status", YesNoEnum.YES.getCode())
                .isNull("delete_time")
                .last("limit 1"));

        // 最新资讯
        List<Article> news = articleMapper.selectList(new QueryWrapper<Article>()
                .select("id,title,image,create_time,update_time")
                .eq("cid", article.getCid())
                .isNull("delete_time")
                .orderByDesc("id")
                .last("limit 8"));

        List<Map<String, Object>> newsList = new LinkedList<>();
        for (Article newArticle : news) {
            Map<String, Object> newsMap = new LinkedHashMap<>();
            newsMap.put("id", newArticle.getId());
            newsMap.put("title", newArticle.getTitle());
            newsMap.put("image", UrlUtils.toAbsoluteUrl(newArticle.getImage()));
            newsMap.put("createTime", TimeUtils.timestampToDate(newArticle.getCreateTime()));
            newsMap.put("updateTime", TimeUtils.timestampToDate(newArticle.getUpdateTime()));
            newsList.add(newsMap);
        }

        // 处理数据
        PcArticleDetailVo vo = new PcArticleDetailVo();
        BeanUtils.copyProperties(article, vo);
        vo.setCreateTime(TimeUtils.timestampToDate(article.getCreateTime()));
        vo.setUpdateTime(TimeUtils.timestampToDate(article.getUpdateTime()));
        vo.setCateName(StringUtils.isNotNull(articleCategory) ? articleCategory.getName() : "");
        vo.setCollect(StringUtils.isNotNull(collect) ? true : false);
        vo.setIsShow(article.getIsShow());
        vo.setClick(article.getClickActual() + article.getClickVirtual());
        vo.setNews(newsList);
        vo.setLast(new JSONArray());
        vo.setNext(new JSONArray());

        if (StringUtils.isNotNull(prev)) {
            Map<String, Object> prevMap = new LinkedHashMap<>();
            prevMap.put("id", prev.getId());
            prevMap.put("title", prev.getTitle());
            vo.setLast(prevMap);
        }

        if (StringUtils.isNotNull(next)) {
            Map<String, Object> nextMap = new LinkedHashMap<>();
            nextMap.put("id", next.getId());
            nextMap.put("title", next.getTitle());
            vo.setNext(nextMap);
        }

        article.setClickActual(article.getClickActual() + 1);
        articleMapper.updateById(article);

        return vo;
    }
    @Override
    public void ocpc(OcpcValidate ocpcValidate){
        String ocpcKey = "OCPC:" + ocpcValidate.getPid() + ":" + ocpcValidate.getId();
        RedisUtils.set(ocpcKey,ocpcValidate.getChannel(), -1);
    }
}
