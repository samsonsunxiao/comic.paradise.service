package com.mdd.common.util;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mdd.common.cache.ConfigCache;
import com.mdd.common.entity.Config;
import com.mdd.common.entity.setting.DevPayConfig;
import com.mdd.common.enums.PaymentEnum;
import com.mdd.common.mapper.ConfigMapper;
import com.mdd.common.mapper.setting.DevPayConfigMapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库配置操作工具
 */
public class ConfigUtils {

    /**
     * 根据类型获取配置
     *
     * @author fzr
     * @param type 类型
     * @return Map<String, String>
     */
    public static Map<String, String> get(String type) {
        Map<String, String> cache = ConfigCache.get(type);
        if (!cache.isEmpty()) {
            return cache;
        }

        ConfigMapper model = SpringUtils.getBean(ConfigMapper.class);
        List<Config> configs = model.selectList(
                new QueryWrapper<Config>()
                        .select("id", "type"+"", "name", "value")
                        .eq("type", type));

        Map<String, String> map = new LinkedHashMap<>();
        for (Config config : configs) {
            map.put(config.getName(), config.getValue());
        }

        ConfigCache.set();
        return map;
    }

    /**
     * 根据类型和名称获取配置
     *
     * @author fzr
     * @param type 类型
     * @param name 名称
     * @return String
     */
    public static String get(String type, String name) {
        String cache = ConfigCache.get(type, name);
        if (!StringUtils.isNull(cache) && !StringUtils.isEmpty(cache)) {
            return cache;
        }

        ConfigMapper model = SpringUtils.getBean(ConfigMapper.class);
        Config config = model.selectOne(
                new QueryWrapper<Config>()
                        .select("id", "type", "name", "value")
                        .eq("name", name)
                        .eq("type", type));

        ConfigCache.set();
        return config.getValue();
    }

    /**
     * 根据类型和名称获取配置
     *
     * @author fzr
     * @param type 类型
     * @param name 名称
     * @return String
     */
    public static String get(String type, String name, String defaults) {
        String cache = ConfigCache.get(type, name);
        if (!StringUtils.isNull(cache) && !StringUtils.isEmpty(cache)) {
            return cache;
        }

        ConfigMapper model = SpringUtils.getBean(ConfigMapper.class);
        Config config = model.selectOne(
                new QueryWrapper<Config>()
                        .select("id", "type", "name", "value")
                        .eq("type", type)
                        .eq("name", name));

        if (config == null) {
            return defaults;
        }

        ConfigCache.set();
        return config.getValue();
    }

    /**
     * 根据类型和名称获取配置(JSON自定转Map)
     *
     * @author fzr
     * @param type 类型
     * @param name 名称
     * @return String
     */
    public static Map<String, String> getMap(String type, String name) {
        String cache = ConfigCache.get(type, name);
        if (!StringUtils.isNull(cache) && !StringUtils.isEmpty(cache)) {
            cache = "[]".equals(cache) ? "{}" : cache;
            return MapUtils.jsonToMap(cache);
        }

        ConfigMapper model = SpringUtils.getBean(ConfigMapper.class);

        Config config = model.selectOne(
                new QueryWrapper<Config>()
                        .select("id", "type", "name", "value")
                        .eq("type", type)
                        .eq("name", name));

        if (config == null) {
            return null;
        }

        if (config.getValue().equals("") || config.getValue().equals("[]") || config.getValue().equals("{}")) {
            return new LinkedHashMap<>();
        }

        ConfigCache.set();
        return MapUtils.jsonToMap(config.getValue());
    }

    /**
     * 设置配置的值
     *
     * @author fzr
     * @param type 类型
     * @param name 名称
     * @param val 值
     */
    public static void set(String type, String name, String val) {
        ConfigMapper model = SpringUtils.getBean(ConfigMapper.class);
        Config config = model.selectOne(
                new QueryWrapper<Config>()
                        .eq("type", type)
                        .eq("name", name));

        if (config != null) {
            config.setValue(val);
            config.setUpdateTime(System.currentTimeMillis() / 1000);
            model.updateById(config);
        } else {
            Config systemConfig = new Config();
            systemConfig.setType(type);
            systemConfig.setName(name);
            systemConfig.setValue(val);
            systemConfig.setCreateTime(System.currentTimeMillis() / 1000);
            systemConfig.setUpdateTime(System.currentTimeMillis() / 1000);
            model.insert(systemConfig);
        }

        ConfigCache.set();
    }

    //返回支付宝配置信息
    public static String getAliDevPay(String field) {
        DevPayConfigMapper model = SpringUtils.getBean(DevPayConfigMapper.class);
        DevPayConfig devPay = model.selectOne(
                new QueryWrapper<DevPayConfig>()
                        .eq("pay_way", PaymentEnum.ALI_PAY.getCode()));
        if (StringUtils.isNull(devPay)) {
            return "";
        } else {
            JSONObject jsonObject = JSONObject.parseObject(devPay.getConfig().toString());
            return StringUtils.isNull(jsonObject.getString(field)) ? "" : jsonObject.getString(field);
        }

    }
}
