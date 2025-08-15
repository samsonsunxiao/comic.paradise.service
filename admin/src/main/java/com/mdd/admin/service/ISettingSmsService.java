package com.mdd.admin.service;

import com.alibaba.fastjson2.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 短信配置接口类
 */
public interface ISettingSmsService {

    /**
     * 短信引擎列表
     *
     * @author fzr
     * @return List<Map<String, Object>>
     */
    List<Map<String, Object>> getConfig();

    /**
     * 短信引擎详情
     *
     * @author fzr
     * @param alias 别名
     * @return Map<String, Object>
     */
    Map<String, Object> detail(String type);

    /**
     * 短信引擎保存
     *
     * @author fzr
     * @param params 参数
     */
    void setConfig(JSONObject params);

}
