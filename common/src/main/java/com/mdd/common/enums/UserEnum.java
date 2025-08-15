package com.mdd.common.enums;

public enum UserEnum {

    // 退款类型
    SEX_OTHER(0, "未知"),
    SEX_MEN(1, "男"),
    SEX_WOMAN(2, "女");

    /**
     * 构造方法
     */
    private final int code;
    private final String msg;
    UserEnum(int code, String msg) {
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
     * 订单类型标识
     *
     * @author fzr
     * @param code 编码
     * @return String
     */
    public static String getSexDesc(Integer code){
        switch (code) {
            case 1:
                return "男";
            case 2:
                return "女";
        }
        return "未知";
    }


}
