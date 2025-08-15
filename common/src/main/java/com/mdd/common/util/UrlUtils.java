package com.mdd.common.util;

import com.mdd.common.config.GlobalConfig;

import java.util.Map;

/**
 * 文件路径处理工具
 */
public class UrlUtils {

    /**
     * 访问前缀
     */
    private static final String uploadPrefix = GlobalConfig.publicPrefix;
    private static final String uploadAdminPrefix = GlobalConfig.adminPublicPrefix;

    /**
     * 转绝对路径
     * 转前: uploads/11.png
     * 转后: https://127.0.0.1/uploads/11.png
     *
     * @author fzr
     * @param url 相对路径
     * @return String
     */
    public static String toAbsoluteUrl(String url) {
        if (url == null || url.equals("")) {
            return "";
        }

        if(url.indexOf("/") != 0) {
            url = "/" + url;
        }

        if (url.startsWith("/api/static/")) {
            return RequestUtils.uri() + url;
        }

        if (url.startsWith("/adminapi/")) {
            return RequestUtils.uri() + url.replace("/adminapi/", "/api/");
        }

        String engine = ConfigUtils.get("storage", "default", "local");
        engine = engine.equals("") ? "local" : engine;
        if (engine.equals("local")) {
            return RequestUtils.uri() + "/" + uploadPrefix + url;
        }

        Map<String, String> config = ConfigUtils.getMap("storage", engine);
        if (config != null) {
            return config.getOrDefault("domain", "") + url;
        }
        return url;
    }

    /**
     * 转绝对路径
     * 转前: uploads/11.png
     * 转后: https://127.0.0.1/uploads/11.png
     *
     * @author fzr
     * @param url 相对路径
     * @return String
     */
    public static String toAdminAbsoluteUrl(String url) {
        if (url == null || url.equals("")) {
            return "";
        }

        if(url.indexOf("/") != 0) {
            url = "/" + url;
        }

        if (url.startsWith("/adminapi/static/")) {
            return RequestUtils.uri() + url;
        }

        if (url.startsWith("/api/static/")) {
            return RequestUtils.uri() + url.replace("/api/static/", "/adminapi/static/");
        }

        String engine = ConfigUtils.get("storage", "default", "local");
        engine = engine.equals("") ? "local" : engine;
        if (engine.equals("local")) {
            if (url.startsWith("/adminapi/uploads/")) {
                return RequestUtils.uri() + url;
            } else {
                return RequestUtils.uri() + "/" + uploadAdminPrefix + url;
            }
        }

        Map<String, String> config = ConfigUtils.getMap("storage", engine);
        if (config != null) {
            return config.getOrDefault("domain", "") + url;
        }
        return url;
    }
    /**
     * 转相对路径
     * 转前: https://127.0.0.1/uploads/11.png
     * 转后: uploads/11.png
     *
     * @author fzr
     * @param url 绝对路径
     * @return String
     */
    public static String toRelativeUrl(String url) {
        if (url == null || url.equals("")) {
            return "";
        }

        String engine = ConfigUtils.get("storage", "default", "local");
        engine = engine.equals("") ? "local" : engine;
        if (engine.equals("local")) {
            return url.replace(RequestUtils.uri(), "")
                      .replace("/" + uploadPrefix + "/", "");
        }

        Map<String, String> config = ConfigUtils.getMap("storage", engine);
        if (config != null) {
            return url.replace(config.getOrDefault("domain", ""), "")
                    .replace( "/" + uploadPrefix + "/", "");
        }

        return url;
    }

    /**
     * 获取存储域名
     * 示例: https://127.0.0.1/
     *
     * @author fzr
     * @return String
     */
    public static String domain() {
        String engine = ConfigUtils.get("storage", "default", "local");
        engine = engine.equals("") ? "local" : engine;
        if (engine.equals("local")) {
            return RequestUtils.uri() + "/";
        }

        Map<String, String> config = ConfigUtils.getMap("storage", engine);
        if (config != null) {
            return config.getOrDefault("domain", "") + "/";
        }
        return "";
    }

    /**
     * 获取域名
     * 示例: https://127.0.0.1/
     *
     * @author fzr
     * @return String
     */
    public static String localDomain(String url){
        return RequestUtils.uri() + url;
    }

    /**
     * 为了避免负载均衡而导致的域名失效，增加该方法
     * @return
     */
    public static String getRequestUrl() {
        if (StringUtils.isNull(YmlUtils.get("app.url"))) {
            return RequestUtils.uri();
        } else {
            return YmlUtils.get("app.url");
        }
    }

    // 获取微信api地址
    public static String getWeixinApiUrl() {
        if (StringUtils.isNull(YmlUtils.get("dev.proxy.host"))) {
            return "https://api.weixin.qq.com";
        } else {
            return "http://" + YmlUtils.get("dev.proxy.host") + ":" + YmlUtils.get("dev.proxy.port");
        }
    }
}
