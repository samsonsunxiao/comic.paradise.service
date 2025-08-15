package com.mdd.common.core;

import com.mdd.common.enums.ErrorEnum;
import lombok.Data;

import java.util.ArrayList;

@Data
public class AjaxResult<T> {

    /** 状态码 **/
    private Integer code;

    /** 提示信息 **/
    private String msg;

    /** 响应数据 **/
    private T data;

    /** 是否显示错误信息 **/
    private Integer show;

    /** 无参构造 **/
    protected AjaxResult() {}

    /**
     * 带参构造
     *
     * @author fzr
     * @param code 状态码
     * @param msg 提示信息
     * @param data 响应数据
     */
    public AjaxResult(Integer code, String msg, T data, Integer show) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.show = show;
    }

    /**
     * 成功返回结果
     *
     * @author fzr
     * @return AjaxResult
     */
    public static AjaxResult<Object> success() {
        return new AjaxResult<>(ErrorEnum.SUCCESS.getCode(), ErrorEnum.SUCCESS.getMsg(), new ArrayList<>(), ErrorEnum.SHOW_MSG.getCode());
    }

    /**
     * 成功返回结果
     *
     * @author fzr
     * @param code 状态码
     * @return AjaxResult
     */
    public static AjaxResult<Object> success(Integer code) {
        return new AjaxResult<>(code, ErrorEnum.SUCCESS.getMsg(), new ArrayList<>(), ErrorEnum.SHOW_MSG.getCode());
    }

    /**
     * 成功返回结果
     *
     * @author fzr
     * @param msg 提示信息
     * @return AjaxResult
     */
    public static AjaxResult<Object> success(String msg) {
        return new AjaxResult<>(ErrorEnum.SUCCESS.getCode(), msg, new ArrayList<>(), ErrorEnum.SHOW_MSG.getCode());
    }

    /**
     * 成功返回结果
     *
     * @author fzr
     * @param msg 提示信息
     * @return AjaxResult
     */
    public static AjaxResult<Object> success(String msg, Integer show) {
        return new AjaxResult<>(ErrorEnum.SUCCESS.getCode(), msg, new ArrayList<>(), show);
    }

    /**
     * 成功返回结果
     *
     * @author fzr
     * @param data 响应数据
     * @return AjaxResult
     */
    public static <T> AjaxResult<T> success(T data) {
        return new AjaxResult<>(ErrorEnum.SUCCESS.getCode(), ErrorEnum.SUCCESS.getMsg(), data, ErrorEnum.HIDE_MSG.getCode());
    }

    /**
     * 成功返回结果
     *
     * @author fzr
     * @param code 状态码
     * @param msg 提示信息
     * @return AjaxResult
     */
    public static AjaxResult<Object> success(Integer code, String msg) {
        return new AjaxResult<>(code, msg, new ArrayList<>(), ErrorEnum.SHOW_MSG.getCode());
    }

    /**
     * 成功返回结果
     *
     * @author fzr
     * @param code 状态码
     * @param msg 提示信息
     * @return AjaxResult
     */
    public static AjaxResult<Object> success(Integer code, String msg, Integer show) {
        return new AjaxResult<>(code, msg, new ArrayList<>(), show);
    }

    public static <T>AjaxResult<T> success(Integer code, T data, Integer show) {
        return new AjaxResult<>(code, ErrorEnum.SUCCESS.getMsg(), data, show);
    }

    /**
     * 成功返回结果
     *
     * @author fzr
     * @param msg 提示信息
     * @param data 响应数据
     * @return AjaxResult
     */
    public static <T> AjaxResult <T> success(String msg, T data) {
        return new AjaxResult<>(ErrorEnum.SUCCESS.getCode(), msg, data, ErrorEnum.SHOW_MSG.getCode());
    }

    /**
     * 成功返回结果
     *
     * @author fzr
     * @param code 状态码
     * @param msg 提示信息
     * @param data 响应数据
     * @return AjaxResult
     */
    public static <T> AjaxResult<T> success(Integer code, String msg, T data) {
        return new AjaxResult<>(code, msg, data, ErrorEnum.SHOW_MSG.getCode());
    }

    /**
     * 响应失败结果
     *
     * @author fzr
     * @param code 状态码
     * @return AjaxResult
     */
    public static AjaxResult<Object> failed(Integer code) {
        return new AjaxResult<>(code, ErrorEnum.FAILED.getMsg(), new ArrayList<>(), ErrorEnum.SHOW_MSG.getCode());
    }

    /**
     * 响应失败结果
     *
     * @author fzr
     * @param msg 提示信息
     * @return AjaxResult
     */
    public static AjaxResult<Object> failed(String msg) {
        return new AjaxResult<>(ErrorEnum.FAILED.getCode(), msg, new ArrayList<>(), ErrorEnum.SHOW_MSG.getCode());
    }

    /**
     * 响应失败结果
     *
     * @author fzr
     * @param data 响应数据
     * @return AjaxResult
     */
    public static <T> AjaxResult<T> failed(T data) {
        return new AjaxResult<T>(ErrorEnum.FAILED.getCode(), ErrorEnum.FAILED.getMsg(), data, ErrorEnum.SHOW_MSG.getCode());
    }

    /**
     * 响应失败结果
     *
     * @author fzr
     * @param code 状态码
     * @param msg 提示信息
     * @return AjaxResult
     */
    public static AjaxResult<Object> failed(Integer code, String msg) {
        return new AjaxResult<>(code, msg, new ArrayList<>(), ErrorEnum.SHOW_MSG.getCode());
    }

    /**
     * 响应失败结果
     *
     * @author fzr
     * @param code 状态码
     * @param msg 提示信息
     * @param data 响应数据
     * @return AjaxResult
     */
    public static <T> AjaxResult<T> failed(Integer code, String msg, T data) {
        return new AjaxResult<>(code, msg, data, ErrorEnum.SHOW_MSG.getCode());
    }

    /**
     * 响应失败结果
     *
     * @author fzr
     * @param code 状态码
     * @param msg 提示信息
     * @param data 响应数据
     * @return AjaxResult
     */
    public static <T> AjaxResult<T> failed(Integer code, String msg, T data, Integer show) {
        return new AjaxResult<>(code, msg, data, show);
    }
}
