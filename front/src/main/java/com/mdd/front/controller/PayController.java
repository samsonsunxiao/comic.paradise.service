package com.mdd.front.controller;

import com.github.binarywang.wxpay.bean.notify.SignatureHeader;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyV3Result;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.mdd.common.aop.NotLogin;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.entity.RechargeOrder;
import com.mdd.common.enums.ClientEnum;
import com.mdd.common.enums.PaymentEnum;
import com.mdd.common.exception.OperateException;
import com.mdd.common.mapper.RechargeOrderMapper;
import com.mdd.common.plugin.wechat.WxPayDriver;
import com.mdd.common.util.MapUtils;
import com.mdd.common.util.StringUtils;
import com.mdd.front.FrontThreadLocal;
import com.mdd.front.service.IPayService;
import com.mdd.front.service.IRechargeService;
import com.mdd.front.validate.InvoiceSearchVaildate;
import com.mdd.front.validate.InvoiceVaildate;
import com.mdd.front.validate.PayVaildate;
import com.mdd.front.validate.PaymentValidate;
import com.mdd.front.vo.pay.AppPayStatusVo;
import com.mdd.front.vo.pay.InvoiceOrderVo;
import com.mdd.front.vo.pay.PayStatusVo;
import com.mdd.front.vo.pay.PayWayListVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequestMapping("/api/pay")
@Api(tags = "支付管理")
public class PayController {

    @Resource
    RechargeOrderMapper rechargeOrderMapper;

    @Resource
    IPayService iPayService;

    @Resource
    IRechargeService iRechargeService;

    @GetMapping("/payWay")
    @ApiOperation("支付方式")
    public AjaxResult<PayWayListVo> payWay(@Validated @NotNull(message = "from参数丢失") @RequestParam String from,
            @Validated @NotNull(message = "orderId参数丢失") @RequestParam Integer order_id) {
        Integer terminal = FrontThreadLocal.getTerminal();

        PayWayListVo list = iPayService.payWay(from, order_id, terminal);
        return AjaxResult.success(list);
    }

    @GetMapping("/payStatus")
    @ApiOperation(("支付状态"))
    public AjaxResult<PayStatusVo> payStatus(@Validated @NotNull(message = "from参数丢失") @RequestParam String from,
            @Validated @NotNull(message = "orderId参数丢失") @RequestParam Integer order_id) {
        PayStatusVo vo = iPayService.payStatus(from, order_id);
        return AjaxResult.success(vo);
    }

    @NotLogin
    @PostMapping("/generateOrder")
    @ApiOperation("生成订单号")
    public AjaxResult<Object> generateOrder(@Validated @RequestBody PayVaildate payVaildate) {
        Integer userId = 0;
        Integer terminal = 2;
        Object payResult = iPayService.generateOrder(payVaildate, userId, terminal);
        return AjaxResult.success(payResult);
    }

    @NotLogin
    @PostMapping("/invoice")
    @ApiOperation("生成发票")
    public AjaxResult<Object> invoice(@Validated @RequestBody InvoiceVaildate invoiceVaildate) {
        Integer userId = 0;
        Integer terminal = 2;
        InvoiceOrderVo result = iPayService.invoice(invoiceVaildate, userId, terminal);
        return AjaxResult.success(result);
    }

    @NotLogin
    @PostMapping("/queryInvoice")
    @ApiOperation("查询发票")
    public AjaxResult<InvoiceOrderVo> queryInvoice(@Validated @RequestBody InvoiceSearchVaildate invoiceVaildate) {
        InvoiceOrderVo result = iPayService.queryInvoice(invoiceVaildate);
        return  AjaxResult.success(result);
    }

    @NotLogin
    @PostMapping("/getqr")
    @ApiOperation("获取支付二维码")
    public AjaxResult<Object> getqr(@Validated @RequestBody PayVaildate payVaildate) {
        Integer userId = 0;
        Integer terminal = 2;
        Object payResult = iPayService.getPayQr(payVaildate, userId, terminal);
        return AjaxResult.success(payResult);
    }

    @NotLogin
    @PostMapping("/check")
    @ApiOperation("获取支付二维码")
    public AjaxResult<AppPayStatusVo> checkpay(@Validated @RequestBody PayVaildate payVaildate) {
        Integer userId = 0;
        Integer terminal = 2;
        AppPayStatusVo payResult = iPayService.checkPay(payVaildate, userId, terminal);
        return AjaxResult.success(payResult);
    }

    @PostMapping("/prepay")
    @ApiOperation("发起支付")
    public AjaxResult<Object> prepay(@Validated @RequestBody PaymentValidate requestObj) {
        // 接收参数
        if (StringUtils.isNull(requestObj.getScene())) {
            requestObj.setScene("recharge");
        }
        String scene = requestObj.getScene();
        Integer payWay = requestObj.getPayWay();
        Integer orderId = requestObj.getOrderId();
        Integer terminal = FrontThreadLocal.getTerminal();
        String code = requestObj.getCode();
        requestObj.setTerminal(terminal);

        // 订单处理
        int payStatus = 0;
        switch (scene) {
            case "recharge":
                RechargeOrder rechargeOrder = rechargeOrderMapper.selectById(orderId);

                Assert.notNull(rechargeOrder, "订单不存在");
                Assert.isTrue(!payWay.equals(PaymentEnum.WALLET_PAY.getCode()), "支付类型不被支持");

                requestObj.setUserId(rechargeOrder.getUserId());
                requestObj.setOutTradeNo(rechargeOrder.getSn());
                requestObj.setOrderAmount(rechargeOrder.getOrderAmount());
                requestObj.setDescription("余额充值");
                requestObj.setAttach("recharge");
                payStatus = rechargeOrder.getPayStatus();

                rechargeOrder.setPayWay(payWay);
                rechargeOrderMapper.updateById(rechargeOrder);
                break;
            case "order":
                break;
        }

        // 订单校验
        if (payStatus != 0) {
            throw new OperateException("订单已支付");
        }
        // 发起支付
        Object result = iPayService.prepay(requestObj, terminal, code);
        return AjaxResult.success(result);
    }

    @NotLogin
    @PostMapping("/notifyMnp")
    @ApiOperation("微信支付回调")
    public AjaxResult<Object> notifyMnp(@RequestBody String jsonData, HttpServletRequest request)
            throws WxPayException {
        // 构建签名
        SignatureHeader signatureHeader = new SignatureHeader();
        signatureHeader.setSignature(request.getHeader("wechatpay-signature"));
        signatureHeader.setNonce(request.getHeader("wechatpay-nonce"));
        signatureHeader.setSerial(request.getHeader("wechatpay-serial"));
        signatureHeader.setTimeStamp(request.getHeader("wechatpay-timestamp"));

        // 解密数据
        WxPayService wxPayService = WxPayDriver.handler(ClientEnum.OA.getCode());
        WxPayOrderNotifyV3Result.DecryptNotifyResult notifyResult = wxPayService
                .parseOrderNotifyV3Result(jsonData, signatureHeader).getResult();

        // 取出数据
        String transactionId = notifyResult.getTransactionId();
        String outTradeNo = notifyResult.getOutTradeNo();
        String attach = notifyResult.getAttach();
        Integer amount = notifyResult.getAmount().getTotal();
        Map<String, Object> attachMap = MapUtils.jsonToMapAsObj(attach);
        log.info("notifyMnp attachMap:{}", attachMap);
        if (attachMap.get("from").toString().equals("vip") || attachMap.get("from").toString().equals("pay")) {

            BigDecimal bigDecimalAmount = new BigDecimal(amount);
            BigDecimal amountInYuan = bigDecimalAmount.divide(new BigDecimal(100));
            iRechargeService.updatePayOrderStatusToPaid(outTradeNo, transactionId, amountInYuan, attachMap.get("param"),
                    attachMap.get("channel").toString(), attachMap.get("ip").toString(),
                    attachMap.get("tagid").toString());
        }
        // 处理回调
        // iPayService.handlePaidNotify(attach, outTradeNo, transactionId);
        return AjaxResult.success();
    }

    // 支付回调通知处理
    @PostMapping("/notify360")
    @NotLogin
    public AjaxResult<Object> notify360(@RequestBody String jsonData, HttpServletRequest request) throws Exception {
        Map<String, Object> dataMap = MapUtils.jsonToMapAsObj(jsonData);
        //log.info("notify360 dataMap:{}", dataMap);
        Integer status = Integer.parseInt(dataMap.get("order_status").toString());
        if (status == 20) {
            BigDecimal bigDecimalAmount = new BigDecimal(dataMap.get("mfr_order_amount").toString());
            BigDecimal amountInYuan = bigDecimalAmount.divide(new BigDecimal(100));
            String orderId = dataMap.get("mfr_order_id").toString();
            String tradeNo = dataMap.get("order_code").toString();
            String channel = dataMap.get("qid").toString();
            log.info("notify360 orderId:{} tradeNo:{} channel:{}", orderId,tradeNo,channel);
            iRechargeService.updatePayOrderStatusToPaid(orderId, tradeNo, amountInYuan, null, channel, "","");
        }
        return AjaxResult.success(200);
    }
}
