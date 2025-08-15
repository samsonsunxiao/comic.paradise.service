package com.mdd.front.controller;

import com.alibaba.fastjson2.JSONObject;
import com.alipay.api.internal.util.AlipaySignature;
import com.mdd.common.aop.NotLogin;
import com.mdd.common.config.AlipayConfig;
import com.mdd.common.util.ConfigUtils;
import com.mdd.front.service.IRechargeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


/**
 * @author panweiliang
 */
@Slf4j
@RestController
@RequestMapping("/api/pay/ali")
public class AliPayController {
    @Resource
    IRechargeService iRechargeService;
    //  支付回调通知处理
    @PostMapping("/notify/order")
    @NotLogin
    public void notifyUrl(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //log.info("异步通知");
        PrintWriter out = response.getWriter();
            try {
                //乱码解决，这段代码在出现乱码时使用
                request.setCharacterEncoding("utf-8");
                //获取支付宝POST过来反馈信息
                Map<String, String> params = new HashMap<>(8);
                Map<String, String[]> requestParams = request.getParameterMap();
                for (Map.Entry<String, String[]> stringEntry : requestParams.entrySet()) {
                    String[] values = stringEntry.getValue();
                    String valueStr = "";
                    for (int i = 0; i < values.length; i++) {
                        valueStr = (i == values.length - 1) ? valueStr + values[i]
                                : valueStr + values[i] + ",";
                    }
                    params.put(stringEntry.getKey(), valueStr);
                }

                //调用SDK验证签名
                boolean signVerified = AlipaySignature.rsaCheckV1(params, ConfigUtils.getAliDevPay("ali_public_key"), AlipayConfig.CHARSET, AlipayConfig.SIGN_TYPE);

                if (!signVerified) {
                    log.error("验签失败");
                    out.print("fail");
                    return;
                }
                // 通知ID
                String notifyId = new String(params.get("notify_id").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                // 通知时间
                String notifyTime = new String(params.get("notify_time").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                //商户订单号,之前生成的带用户ID的订单号
                String outTradeNo = new String(params.get("out_trade_no").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                //支付宝交易号
                String tradeNo = new String(params.get("trade_no").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                //付款金额
                String totalAmount = new String(params.get("total_amount").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                //交易状态
                String tradeStatus = new String(params.get("trade_status").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

                //自定义参数
                JSONObject passbackParams = JSONObject.parseObject(new String(params.get("passback_params").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
                String from = passbackParams.getString("from");
                String channel = passbackParams.getString("channel");
                String ipstr = passbackParams.getString("ip");
                String tagid = passbackParams.getString("tagid");
                /*
                 * 交易状态
                 * TRADE_SUCCESS 交易完成
                 * TRADE_FINISHED 支付成功
                 * WAIT_BUYER_PAY 交易创建
                 * TRADE_CLOSED 交易关闭
                 */
                //log.info("tradeStatus:" + tradeStatus);
                Object param = passbackParams.get("param");
                if ("TRADE_FINISHED".equals(tradeStatus)) {
                    /*此处可自由发挥*/
                    //判断该笔订单是否在商户网站中已经做过处理
                    //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                    //如果有做过处理，不执行商户的业务程序
                    //注意：
                    //退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
                } else if ("TRADE_SUCCESS".equals(tradeStatus)) {
                    if (from.equals("recharge") || from.equals("vip") || from.equals("pay")) {
                        BigDecimal amount = new BigDecimal(totalAmount);
                        iRechargeService.updatePayOrderStatusToPaid(outTradeNo, tradeNo, amount, param, channel, ipstr, tagid);
                    }

                    if (from.equals("member")) {
                        return;
                    }
                }
            } finally {
                out.print("fail");
            }
        //out.print("success");
    }
}

