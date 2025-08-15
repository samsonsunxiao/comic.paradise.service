package com.mdd.common.enums;

import com.alibaba.fastjson2.JSONObject;
import com.mdd.common.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 通知枚举类
 */
public enum NoticeEnum {

    STATUS_WAIT(0, "等待"),
    STATUS_OK(1, "成功"),
    STATUS_FAIL(2, "失败"),

    VIEW_UNREAD(0, "未读"),
    VIEW_READ(0, "已读"),

    SENDER_SYS(1, "系统类型"),
    SENDER_SMS(2, "短信类型"),
    SENDER_MNP(3, "小程序类型"),
    SENDER_OA(4, "公众号类型"),

    LOGIN_CAPTCHA(101, "登录验证码"),
    BIND_MOBILE_CAPTCHA(102, "绑定手机验证码"),
    CHANGE_MOBILE_CAPTCHA(103, "变更手机验证码"),
    FIND_LOGIN_PASSWORD_CAPTCHA(104, "找回登录密码验证码");

    /**
     * 构造方法
     */
    private final int code;
    private final String msg;
    NoticeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 获取状态码
     *
     * @author fzr
     * @return Long
     */
    public int getCode() {
        return this.code;
    }

    /**
     * 获取提示
     *
     * @author fzr
     * @return String
     */
    public String getMsg() {
        return this.msg;
    }

    /**
     * @notes 更具标记获取场景
     * @param $tag
     * @return int|string
     * @author damonyuan
     */
    public static Integer getSceneByTag( String tag ) {
        JSONObject scene = new JSONObject();
        // 手机验证码登录
        scene.put("YZMDL", LOGIN_CAPTCHA.getCode());
        // 绑定手机号验证码
        scene.put("BDSJHM", BIND_MOBILE_CAPTCHA.getCode());
        // 变更手机号验证码
        scene.put("BGSJHM", CHANGE_MOBILE_CAPTCHA.getCode());
        // 找回登录密码
        scene.put("ZHDLMM", FIND_LOGIN_PASSWORD_CAPTCHA.getCode());

        return scene.getInteger(tag);
    }

    /**
     * 验证码场景
     */
    public static List<Integer> getSmsScene() {
        List<Integer> ret = new ArrayList<>();
        ret.add(LOGIN_CAPTCHA.getCode());
        ret.add(BIND_MOBILE_CAPTCHA.getCode());
        ret.add(CHANGE_MOBILE_CAPTCHA.getCode());
        ret.add(FIND_LOGIN_PASSWORD_CAPTCHA.getCode());
        return ret;
    }

}
