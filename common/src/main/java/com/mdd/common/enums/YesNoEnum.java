package com.mdd.common.enums;

/**
 * 相册枚举
 */
public enum YesNoEnum {

    YES(1, "是"),
    NO(0, "否");

    /**
     * 构造方法
     */
    private final int code;
    private final String msg;
    YesNoEnum(int code, String msg) {
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
