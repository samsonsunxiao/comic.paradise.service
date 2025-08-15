package com.mdd.common.exception;

import com.mdd.common.enums.ErrorEnum;

/**
 * 支付失败异常
 */
public class PaymentException extends BaseException {

    public PaymentException(String msg) {
        super(ErrorEnum.FAILED.getCode(), msg, ErrorEnum.SHOW_MSG.getCode());
    }

    public PaymentException(String msg, Integer errCode) {
        super(errCode, msg, ErrorEnum.SHOW_MSG.getCode());
    }

}
