package com.mdd.common.enums;

import java.util.LinkedHashMap;
import java.util.Map;

public enum LogMoneyEnum {

    /**
     * 用户余额类型
     */
    UM_INC_ADMIN(200, "平台增加余额"),
    UM_DEC_ADMIN(100, "平台减少余额"),
    UM_INC_RECHARGE(201, "充值增加余额"),
    UM_DEC_RECHARGE(101, "充值订单退款减少余额");

    /**
     * 构造方法
     */
    private final int code;
    private final String msg;
    LogMoneyEnum(int code, String msg) {
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
     * 根据编码获取Msg
     *
     * @author fzr
     * @param code 类型
     * @return String
     */
    public static String getMsgByCode(Integer code){
        for(LogMoneyEnum enumItem : LogMoneyEnum.values()) {
            if (enumItem.getCode() == code) {
                return enumItem.getMsg();
            }
        }
        return null;
    }

    /**
     * 获取所有类型
     *
     * @author fzr
     * @return String
     */
    public static Map<Integer, String> getTypeList(){
        Map<Integer, String> map = new LinkedHashMap<>();
        for(LogMoneyEnum enumItem : LogMoneyEnum.values()) {
            map.put(enumItem.getCode(), enumItem.getMsg());
        }
        return map;
    }

}
