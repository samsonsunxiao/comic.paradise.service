package com.mdd.admin.service.impl;

import com.mdd.admin.service.ISettingWebsiteService;
import com.mdd.admin.validate.setting.SettingWebsiteValidate;
import com.mdd.admin.vo.setting.SettingWebsiteVo;
import com.mdd.common.util.ConfigUtils;
import com.mdd.common.util.UrlUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 网站信息配置服务实现类
 */
@Service
public class SettingWebsiteServiceImpl implements ISettingWebsiteService {

    /**
     * 获取网站信息
     *
     * @author fzr
     * @return Map<String, String>
     */
    @Override
    public SettingWebsiteVo getWebsite() {
        Map<String, String> config = ConfigUtils.get("website");
        SettingWebsiteVo vo = new SettingWebsiteVo();
        vo.setName(config.getOrDefault("name", ""));
        vo.setWebFavicon(UrlUtils.toAdminAbsoluteUrl(config.getOrDefault("web_favicon", "")));
        vo.setWebLogo(UrlUtils.toAdminAbsoluteUrl(config.getOrDefault("web_logo", "")));
        vo.setLoginImage(UrlUtils.toAdminAbsoluteUrl(config.getOrDefault("login_image", "")));
        vo.setShopName(config.getOrDefault("shop_name", ""));
        vo.setShopLogo(UrlUtils.toAdminAbsoluteUrl(config.getOrDefault("shop_logo", "")));
        vo.setPcLogo(UrlUtils.toAdminAbsoluteUrl(config.getOrDefault("pc_logo", "")));
        vo.setPcTitle(config.getOrDefault("pc_title", ""));
        vo.setPcIco(UrlUtils.toAdminAbsoluteUrl(config.getOrDefault("pc_ico", "")));
        vo.setPcDesc(config.getOrDefault("pc_desc", ""));
        vo.setPcKeywords(config.getOrDefault("pc_keywords", ""));
        vo.setH5Favicon(UrlUtils.toAdminAbsoluteUrl(config.getOrDefault("h5_favicon", "")));
        return vo;
    }

    /**
     * 保存网站信息
     *
     * @author fzr
     * @param websiteValidate 参数
     */
    @Override
    public void setWebsite(SettingWebsiteValidate websiteValidate) {
        ConfigUtils.set("website", "name", websiteValidate.getName());
        ConfigUtils.set("website", "web_logo", UrlUtils.toRelativeUrl(websiteValidate.getWebLogo()));
        ConfigUtils.set("website", "web_favicon", UrlUtils.toRelativeUrl(websiteValidate.getWebFavicon()));
        ConfigUtils.set("website", "login_image", UrlUtils.toRelativeUrl(websiteValidate.getLoginImage()));
        ConfigUtils.set("website", "h5_favicon", UrlUtils.toRelativeUrl(websiteValidate.getH5Favicon()));

        ConfigUtils.set("website", "shop_name", websiteValidate.getShopName());
        ConfigUtils.set("website", "shop_logo", UrlUtils.toRelativeUrl(websiteValidate.getShopLogo()));

        ConfigUtils.set("website", "pc_logo", UrlUtils.toRelativeUrl(websiteValidate.getPcLogo()));
        ConfigUtils.set("website", "pc_ico", UrlUtils.toRelativeUrl(websiteValidate.getPcIco()));
        ConfigUtils.set("website", "pc_title", websiteValidate.getPcTitle());
        ConfigUtils.set("website", "pc_desc", websiteValidate.getPcDesc());
        ConfigUtils.set("website", "pc_keywords", websiteValidate.getPcKeywords());
    }

}
