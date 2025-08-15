package com.mdd.admin.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.mdd.admin.service.ISettingStorageService;
import com.mdd.common.util.ConfigUtils;
import com.mdd.common.util.MapUtils;
import com.mdd.common.util.StringUtils;
import com.mdd.common.util.YmlUtils;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 存储配置服务实现类
 */
@Service
public class SettingStorageServiceImpl implements ISettingStorageService {

    /**
     * 存储列表
     *
     * @author fzr
     * @return List<Map<String, Object>>
     */
    @Override
    public List<Map<String, Object>> list() {
        String engine = ConfigUtils.get("storage", "default", "local");
        List<Map<String, Object>> list = new LinkedList<>();

        Map<String, Object> local = new LinkedHashMap<>();
        local.put("name", "本地存储");
        local.put("engine", "local");
        local.put("path", "存储在本地服务器");
        local.put("status", engine.equals("local") ? 1 : 0);
        list.add(local);

        Map<String, Object> qiniu = new LinkedHashMap<>();
        qiniu.put("name", "七牛云存储");
        qiniu.put("engine", "qiniu");
        qiniu.put("path", "存储在七牛云，请前往七牛云开通存储服务");
        qiniu.put("status", engine.equals("qiniu") ? 1 : 0);
        list.add(qiniu);

        Map<String, Object> aliyun = new LinkedHashMap<>();
        aliyun.put("name", "阿里云OSS");
        aliyun.put("engine", "aliyun");
        aliyun.put("path", "存储在阿里云，请前往阿里云开通存储服务");
        aliyun.put("status", engine.equals("aliyun") ? 1 : 0);
        list.add(aliyun);

        Map<String, Object> qcloud = new LinkedHashMap<>();
        qcloud.put("name", "腾讯云COS");
        qcloud.put("engine", "qcloud");
        qcloud.put("path", "存储在腾讯云，请前往腾讯云开通存储服务");
        qcloud.put("status", engine.equals("qcloud") ? 1 : 0);
        list.add(qcloud);

        return list;
    }

    /**
     * 存储详情
     *
     * @author fzr
     * @param engine 存储别名
     * @return Map<String, Object>
     */
    @Override
    public Map<String, Object> detail(String engine) {
        String env = YmlUtils.get("like.production");
        boolean envStatus = StringUtils.isNotNull(env) && env.equals("true");

        String defaultEngine = ConfigUtils.get("storage", "default", "local");
        Map<String, String> config = ConfigUtils.getMap("storage", engine);
        config = StringUtils.isNotNull(config) ? config : Collections.emptyMap();

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", config.getOrDefault("name", ""));
        map.put("engine", engine);
        map.put("status", defaultEngine.equals(engine) ? 1 : 0);
        if (!engine.equals("local")) {
            map.put("bucket", config.getOrDefault("bucket", ""));
            // map.put("secret_key", envStatus ? "******" : config.getOrDefault("secret_key", ""));
            // map.put("access_key", envStatus ? "******" : config.getOrDefault("access_key", ""));
            map.put("domain", config.getOrDefault("domain", ""));
            if (engine.equals("qcloud")) {
                map.put("region", config.getOrDefault("region", ""));
            }
            if (engine.equals("aliyun")) {
                map.put("endpoint", config.getOrDefault("endpoint", ""));
                map.put("config", MapUtils.jsonToMap(config.getOrDefault("config", "")));
                map.put("region", config.getOrDefault("region", ""));
            }
        }

        return map;
    }

    /**
     * 存储编辑
     *
     * @author fzr
     * @param params 参数
     */
    @Override
    public void setup(Map<String, Object> params) {
        Assert.notNull(params.get("engine"), "engine参数缺失");
        Assert.notNull(params.get("status"), "status参数缺失");
        Map<String, Object> map = new LinkedHashMap<>();

        map.put("name", "本地存储");
        if (!params.get("engine").toString().equals("local")) {
            map.put("bucket", params.getOrDefault("bucket", "").toString());
            // map.put("secret_key", params.getOrDefault("secret_key", "").toString());
            // map.put("access_key", params.getOrDefault("access_key", "").toString());
            map.put("domain", params.getOrDefault("domain", "").toString());
            switch (params.get("engine").toString()) {
                case "qcloud":
                    map.put("name", "腾讯云存储");
                    map.put("region", params.getOrDefault("region", "").toString());
                    break;
                case "qiniu":
                    map.put("name", "七牛云存储");
                    break;
                case "aliyun":
                    map.put("name", "阿里云存储");
                    map.put("endpoint", params.getOrDefault("endpoint", "").toString());
                    map.put("region", params.getOrDefault("region", "").toString());
                    map.put("config", params.getOrDefault("config", ""));
                    break;
            }
        }

        ConfigUtils.set("storage", params.get("engine").toString(), JSON.toJSONString(map));

        String engine = ConfigUtils.get("storage", "default", "local");
        if (Integer.parseInt(params.get("status").toString()) == 1) {
            ConfigUtils.set("storage", "default", params.get("engine").toString());
        } else if (engine.equals(params.get("engine")) && Integer.parseInt(params.get("status").toString()) == 0) {
            ConfigUtils.set("storage", "default", "");
        }
    }

    /**
     * 引擎切换
     *
     * @author fzr
     * @param alias 引擎别名
     * @param status 状态
     */
    @Override
    public void change(String alias, Integer status) {
        String engine = ConfigUtils.get("storage", "default", "local");
        if (engine.equals(alias) && status == 0) {
            ConfigUtils.set("storage", "default", "");
        } else {
            ConfigUtils.set("storage", "default", alias);
        }
    }

}
