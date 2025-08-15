package com.mdd.admin.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.mdd.admin.service.IIndexService;
import com.mdd.common.config.GlobalConfig;
import com.mdd.common.util.*;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 主页服务实现类
 */
@Service
public class IndexServiceImpl implements IIndexService {

    /**
     * 控制台数据
     *
     * @author fzr
     * @return Map<String, Object>
     */
    @Override
    public Map<String, Object> index() {
        Map<String, Object> console = new LinkedHashMap<>();

        // 版本信息
        Map<String, Object> version = new LinkedHashMap<>();
        version.put("name", ConfigUtils.get("website", "name", "LikeAdmin-Java"));
        version.put("version", GlobalConfig.version);
        version.put("website", "www.likeadmin.cn");
        version.put("based", "Vue3.x、ElementUI、MySQL");
        Map<String, String> channel = new LinkedHashMap<>();
        channel.put("gitee", "https://gitee.com/likeadmin/likeadmin_java");
        channel.put("website", "https://www.likeadmin.cn");
        version.put("channel", channel);
        console.put("version", version);

        // 今日数据
        Map<String, Object> today = new LinkedHashMap<>();
        today.put("time", "2022-08-11 15:08:29");
        today.put("today_visitor", 10);   // 访问量(人)
        today.put("total_visitor", 100);  // 总访问量
        today.put("today_sales", 30);    // 销售额(元)
        today.put("total_sales", 65);    // 总销售额
        today.put("order_num", 12);    // 订单量(笔)
        today.put("order_sum", 255);   // 总订单量
        today.put("today_new_user", 120);   // 新增用户
        today.put("total_new_user", 360);   // 总访用户
        console.put("today", today);

        // 访客图表
        Map<String, Object> visitor = new LinkedHashMap<>();
        visitor.put("date", TimeUtils.daysAgoDate(15));
        visitor.put("list", new JSONArray() {{
            add(new JSONObject() {{
                put("name", "访客数");
                put("data", Arrays.asList(12,13,11,5,8,22,14,9,456,62,78,12,18,22,46));
            }});
        }});
        console.put("visitor", visitor);

        console.put("menu", new JSONArray() {{

            add(new JSONObject() {{
                put("name", "管理员");
                put("image", UrlUtils.toAdminAbsoluteUrl("/api/static/menu_admin.png"));
                put("url", "/permission/admin");
            }});

            add(new JSONObject() {{
                put("name", "角色管理");
                put("image", UrlUtils.toAdminAbsoluteUrl("/api/static/menu_role.png"));
                put("url", "/permission/role");
            }});

            add(new JSONObject() {{
                put("name", "部门管理");
                put("image", UrlUtils.toAdminAbsoluteUrl("/api/static/menu_dept.png"));
                put("url", "/organization/department");
            }});

            add(new JSONObject() {{
                put("name", "字典管理");
                put("image", UrlUtils.toAdminAbsoluteUrl("/api/static/menu_dict.png"));
                put("url", "/dev_tools/dict");
            }});

            add(new JSONObject() {{
                put("name", "代码生成器");
                put("image", UrlUtils.toAdminAbsoluteUrl("/api/static/menu_generator.png"));
                put("url", "/dev_tools/code");
            }});

            add(new JSONObject() {{
                put("name", "素材中心");
                put("image", UrlUtils.toAdminAbsoluteUrl("/api/static/menu_file.png"));
                put("url", "/material/index");
            }});

            add(new JSONObject() {{
                put("name", "菜单权限");
                put("image", UrlUtils.toAdminAbsoluteUrl("/api/static/menu_auth.png"));
                put("url", "/permission/menu");
            }});

            add(new JSONObject() {{
                put("name", "网站信息");
                put("image", UrlUtils.toAdminAbsoluteUrl("/api/static/menu_website.png"));
                put("url", "/setting/website/information");
            }});

        }});


        return console;
    }

    /**
     * 公共配置
     *
     * @author fzr
     * @return Map<String, Object>
     */
    @Override
    public Map<String, Object> config() {
        Map<String, String> website   = ConfigUtils.get("website");
        String copyright = ConfigUtils.get("copyright", "config", "");

        String captchaStatus = YmlUtils.get("like.captcha.status");

        Map<String, Object> map = new LinkedHashMap<>();
        // 文件域名
        map.put("oss_domain", UrlUtils.domain());
        //map.put("loginCaptcha", StringUtils.isNotNull(captchaStatus) && captchaStatus.equals("true"));
        // 网站名称
        map.put("web_name", website.getOrDefault("name", ""));
        // 网站图标
        map.put("web_favicon", UrlUtils.toAdminAbsoluteUrl(website.getOrDefault("web_favicon", "")));
        // 网站logo
        map.put("web_logo", UrlUtils.toAdminAbsoluteUrl(website.getOrDefault("web_logo", "")));
        // 登录页
        map.put("login_image", UrlUtils.toAdminAbsoluteUrl(website.getOrDefault("login_image", "")));
        // 版权信息
        map.put("copyright_config", ListUtils.stringToListAsMapStr(copyright));
        return map;
    }

}
