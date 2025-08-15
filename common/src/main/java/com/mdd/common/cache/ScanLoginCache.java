package com.mdd.common.cache;

import com.mdd.common.util.RedisUtils;
import com.mdd.common.util.StringUtils;

/**
 * 微信扫码登录缓存
 */
public class ScanLoginCache {

    private static final String KEY = "wechat:scan:login:";

    public static String get(String sessionId) {
        return get(sessionId, true);
    }

    public static String get(String sessionId, Boolean isDel) {
        Object o = RedisUtils.get(KEY+sessionId);
        if (StringUtils.isNull(o)) {
            return "";
        }
        if (isDel){
            RedisUtils.del(KEY+sessionId);
        }
        return o.toString();
    }

    public static void set(String sessionId, String state) {
        RedisUtils.set(KEY+sessionId, state, 600);
    }

}
