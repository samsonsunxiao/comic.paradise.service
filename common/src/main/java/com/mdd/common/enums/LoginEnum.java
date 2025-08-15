package com.mdd.common.enums;

/**
 * 相册枚举
 */
public enum LoginEnum {

    ACCOUNT_PASSWORD(1, "账号/手机号密码登录"),
    MOBILE_CAPTCHA(2, "手机验证码登录"),

    THIRD_LOGIN(3, "第三方登录");

    /**
     * 构造方法
     */
    private final int code;
    private final String msg;
    LoginEnum(int code, String msg) {
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

}
