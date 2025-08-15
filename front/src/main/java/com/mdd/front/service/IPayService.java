package com.mdd.front.service;

import com.alibaba.fastjson2.JSONObject;
import com.alipay.api.AlipayApiException;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.mdd.front.validate.InvoiceSearchVaildate;
import com.mdd.front.validate.InvoiceVaildate;
import com.mdd.front.validate.PayVaildate;
import com.mdd.front.validate.PaymentValidate;
import com.mdd.front.vo.pay.AppPayStatusVo;
import com.mdd.front.vo.pay.InvoiceOrderVo;
import com.mdd.front.vo.pay.PayStatusVo;
import com.mdd.front.vo.pay.PayWayListVo;


/**
 * 支付接口服务类
 */
public interface IPayService {

    /**
     * 支付方式
     *
     * @author fzr
     * @param from 场景
     * @param orderId 订单ID
     * @param terminal 终端
     * @return List<PayWayListedVo>
     */
    PayWayListVo payWay(String from, Integer orderId, Integer terminal);

    /**
     * 支付状态
     *
     * @author fzr
     * @param from 场景
     * @param orderId 订单ID
     * @return PayStatusVo
     */
    PayStatusVo payStatus(String from, Integer orderId);

    Object getPayQr(PayVaildate PayVaildate, Integer userId, Integer terminal);

    Object generateOrder(PayVaildate PayVaildate, Integer userId, Integer terminal);

    InvoiceOrderVo invoice(InvoiceVaildate invoiceVaildate, Integer userId, Integer terminal);

    InvoiceOrderVo queryInvoice(InvoiceSearchVaildate invoiceSearchVaildate);

    AppPayStatusVo checkPay(PayVaildate PayVaildate, Integer userId, Integer terminal);
    /**
     * 发起支付
     *
     * @param params 参数
     * @param terminal 终端
     * @return Object
     */
    Object prepay(PaymentValidate params, Integer terminal, String code);

    /**
     * 支付回调处理
     *
     * @param attach 场景码
     * @param outTradeNo 订单编号
     * @param transactionId 流水号
     */
    void handlePaidNotify(String attach, String outTradeNo, String transactionId) throws WxPayException;

    JSONObject createAliH5Order(PaymentValidate params) throws AlipayApiException;

}
