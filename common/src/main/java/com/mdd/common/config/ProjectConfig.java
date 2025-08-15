package com.mdd.common.config;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局配置
 */
public class ProjectConfig {
    // 列表页
    public static Map<String, Integer> lists = new HashMap<String, Integer>(){{
        put("pageSizeMax", 25000);
        put("pageSize", 25);
    }};
}
