package com.mdd.front.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.mdd.common.config.GlobalConfig;
import com.mdd.common.entity.article.Article;
import com.mdd.common.entity.decorate.DecoratePage;
import com.mdd.common.entity.decorate.DecorateTabbar;
import com.mdd.common.entity.setting.HotSearch;
import com.mdd.common.mapper.article.ArticleMapper;
import com.mdd.common.mapper.decorate.DecoratePageMapper;
import com.mdd.common.mapper.decorate.DecorateTabbarMapper;
import com.mdd.common.mapper.setting.HotSearchMapper;
import com.mdd.common.util.*;
import com.mdd.front.service.IDecorateTabbarService;
import com.mdd.front.service.IIndexService;
import com.mdd.front.vo.decorateTabbar.DecorateTabbarVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 首页服务实现类
 */
@Service
public class IndexServiceImpl implements IIndexService {

    @Resource
    IDecorateTabbarService iDecorateTabbarService;
    @Resource
    DecoratePageMapper decoratePageMapper;

    @Resource
    DecorateTabbarMapper decorateTabbarMapper;

    @Resource
    HotSearchMapper hotSearchMapper;

    @Resource
    ArticleMapper articleMapper;

    /**
     * 首页
     *
     * @author fzr
     * @return Map<String, Object>
     */
    @Override
    public Map<String, Object> index() {
        Map<String, Object> response = new LinkedHashMap<>();
        DecoratePage decoratePage = decoratePageMapper.selectOne(
                new QueryWrapper<DecoratePage>()
                    .eq("id", 1)
                    .last("limit 1"));

        List<Map<String, Object>> articleList = new LinkedList<>();
        List<Article> articles = articleMapper.selectList(new QueryWrapper<Article>()
                .eq("is_show", 1)
                .isNull("delete_time")
                .orderByDesc("id")
                .last("limit 20"));

        for (Article article : articles) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", article.getId());
            map.put("title", article.getTitle());
            map.put("desc", article.getDesc());
            map.put("abstract", article.getAbstractField());
            map.put("image", UrlUtils.toAbsoluteUrl(article.getImage()));
            map.put("author", article.getAuthor());
            map.put("click", article.getClickActual() + article.getClickVirtual());
            map.put("create_time", TimeUtils.timestampToDate(article.getCreateTime()));
            articleList.add(map);
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
                    if (StringUtils.isNotNull(tempJSON.getString("bg"))) {
                        tempJSON.put("bg", UrlUtils.toAbsoluteUrl(tempJSON.getString("bg")));
                    }
                    if (StringUtils.isNotNull(tempJSON.getString("qrcode"))) {
                        tempJSON.put("qrcode", UrlUtils.toAbsoluteUrl(tempJSON.getString("qrcode")));
                    }
                }
            } else {
                for (int j = 0; j < tempArray.size(); j++) {
                    JSONObject tempImageJson = tempArray.getJSONObject(j);
                    tempImageJson.put("image", UrlUtils.toAbsoluteUrl(tempImageJson.getString("image")));
                    tempImageJson.put("bg", UrlUtils.toAbsoluteUrl(tempImageJson.getString("bg")));
                }
            }
        }
        decoratePage.setData(dataArray.toJSONString());

        response.put("domain", UrlUtils.domain());
        response.put("page", decoratePage);
        response.put("article", articleList);
        return response;
    }

    /**
     * 装修
     *
     * @author fzr
     * @param id 主键
     * @return Map<String, Object>
     */
    @Override
    public Map<String, Object> decorate(Integer id) {
        DecoratePage decoratePage = decoratePageMapper.selectOne(
                new QueryWrapper<DecoratePage>()
                        .eq("id", id)
                        .last("limit 1"));

        Assert.notNull(decoratePage, "数据不存在!");

        String data = decoratePage.getData();
        if (StringUtils.isJsonArrayString(data)) {
            JSONArray dataArray = JSONArray.parseArray(data);
            for (int i = 0; i < dataArray.size(); i++) {
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
                        if (StringUtils.isNotNull(tempJSON.getString("bg"))) {
                            tempJSON.put("bg", UrlUtils.toAbsoluteUrl(tempJSON.getString("bg")));
                        }
                        if (StringUtils.isNotNull(tempJSON.getString("qrcode"))) {
                            tempJSON.put("qrcode", UrlUtils.toAbsoluteUrl(tempJSON.getString("qrcode")));
                        }
                    }
                } else {
                    for (int j = 0; j < tempArray.size(); j++) {
                        JSONObject tempImageJson = tempArray.getJSONObject(j);
                        tempImageJson.put("image", UrlUtils.toAbsoluteUrl(tempImageJson.getString("image")));
                        tempImageJson.put("bg", UrlUtils.toAbsoluteUrl(tempImageJson.getString("bg")));
                    }
                }
            }
            decoratePage.setData(dataArray.toJSONString());
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("type", decoratePage.getType());
        response.put("name", decoratePage.getName());
        response.put("data", decoratePage.getData());
        response.put("meta", decoratePage.getMeta());
        return response;
    }

    /**
     * 配置
     *
     * @author fzr
     * @return Map<String, Object>
     */
    @Override
    public Map<String, Object> config() {
        Map<String, Object> response = new LinkedHashMap<>();

        // 底部导航
        List<DecorateTabbarVo> tabbar = iDecorateTabbarService.getTabbarLists();

        // 导航颜色
        JSONObject style = JSONObject.parse(ConfigUtils.get("tabbar", "style"));

        // 登录配置
        JSONObject loginMap = new JSONObject();
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

        // 网站信息
        JSONObject website = new JSONObject();
        website.put("h5_favicon",  UrlUtils.toAbsoluteUrl(ConfigUtils.get("website", "h5_favicon", "")));
        website.put("shop_name", ConfigUtils.get("website", "shop_name", ""));
        website.put("shop_logo", UrlUtils.toAbsoluteUrl(ConfigUtils.get("website", "shop_logo", "")));

        // 备案信息
        Map<String, String> websiteConfig = ConfigUtils.get("copyright");
        String copyright = websiteConfig.getOrDefault("config", "[]");
        List<Map<String, String>> copyrightMap = ListUtils.stringToListAsMapStr(copyright);

        // H5配置
        JSONObject webPage = new JSONObject();
        webPage.put("status", Integer.valueOf(ConfigUtils.get("web_page", "status", "1")));
        webPage.put("page_status", Integer.valueOf(ConfigUtils.get("web_page", "page_status", "0")));
        webPage.put("page_url", ConfigUtils.get("web_page", "page_url", ""));
        webPage.put("url", UrlUtils.domain() + "/mobile");

        response.put("domain", UrlUtils.domain());
        response.put("style", style);
        response.put("tabbar", tabbar);
        response.put("login", loginMap);
        response.put("website", website);
        response.put("webPage", webPage);
        response.put("version", GlobalConfig.version);
        response.put("copyright", copyrightMap);
        return response;
    }

    /**
     * 政策
     *
     * @author fzr
     * @param type 类型 service=服务协议,privacy=隐私协议
     * @return Map<String, Object>
     */
    @Override
    public Map<String, String> policy(String type) {
        Map<String, String> map = new HashMap<>();
        map.put("title", ConfigUtils.get("agreement", type + "_title", ""));
        map.put("content", ConfigUtils.get("agreement", type + "_content", ""));
        return map;
    }

    /**
     * 热搜
     *
     * @author fzr
     * @return List<String>
     */
    @Override
    public List<String> hotSearch() {
        String isHotSearch = ConfigUtils.get("search", "isHotSearch", "0");

        List<String> list = new LinkedList<>();
        if (Integer.parseInt(isHotSearch) == 1) {
            List<HotSearch> hotSearches = hotSearchMapper.selectList(
                    new QueryWrapper<HotSearch>()
                        .orderByDesc(Arrays.asList("sort", "id")));

            for (HotSearch hotSearch : hotSearches) {
                list.add(hotSearch.getName());
            }
        }

        return list;
    }

}
