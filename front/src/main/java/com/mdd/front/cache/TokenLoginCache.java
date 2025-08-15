package com.mdd.front.cache;

import com.mdd.common.util.RedisUtils;
import com.mdd.common.util.StringUtils;
import com.mdd.common.util.YmlUtils;
import com.mdd.front.FrontThreadLocal;

/**
 * 登录令牌缓存
 */
public class TokenLoginCache {

    private static final String KEY = "login:front:token:";

    public static Integer get() {
        Integer terminal = FrontThreadLocal.getTerminal();
        String token = FrontThreadLocal.getToken();
        String key = KEY + terminal + ":" + token;
        Object o = RedisUtils.get(key);
        if (StringUtils.isNull(o)) {
            return 0;
        }

        return Integer.parseInt(o.toString());
    }

    public static void set(String token, Object id) {
        int timeout = 7200;
        if (StringUtils.isNotNull(YmlUtils.get("like.login.timeout"))) {
            timeout = Integer.parseInt(YmlUtils.get("like.login.timeout"));
        }

        Integer terminal = FrontThreadLocal.getTerminal();
        String key = KEY + terminal + ":" + token;
        RedisUtils.set(key, id, timeout);
    }

    public static void set(String token, Object id, String terminal) {
        int timeout = 7200;
        if (StringUtils.isNotNull(YmlUtils.get("like.login.timeout"))) {
            timeout = Integer.parseInt(YmlUtils.get("like.login.timeout"));
        }
        String key = KEY + terminal + ":" + token;
        RedisUtils.set(key, id, timeout);
    }

    public static void del() {
        Integer terminal = FrontThreadLocal.getTerminal();
        String token = FrontThreadLocal.getToken();
        del(terminal, token);
    }

    public static void del(Integer terminal, String token) {
        String key = KEY + terminal + ":" + token;
        System.out.println(key);
        RedisUtils.del(key);
    }

}
