package com.mdd.common.enums;

/**
 * 相册枚举
 */
public enum FileEnum {

    IMAGE_TYPE(10, "图片类型"),
    VIDEO_TYPE(20, "视频类型"),
    FILE_TYPE(30, "文件类型"),

    // 图片来源
    SOURCE_ADMIN(0, "后台"),
    SOURCE_USER(1, "用户");

    /**
     * 构造方法
     */
    private final int code;
    private final String msg;
    FileEnum(int code, String msg) {
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
