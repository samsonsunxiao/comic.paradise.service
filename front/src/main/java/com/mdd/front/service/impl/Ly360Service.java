package com.mdd.front.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.mdd.common.exception.OperateException;
import com.mdd.common.util.HttpUtils;
import com.mdd.common.util.StringUtils;
import com.mdd.common.util.ToolUtils;
import com.mdd.common.util.YmlUtils;

public class Ly360Service {

    private Map<String, String> transferInvoice(Map<String, String> invoiceMap, long timestamp, String accessToken, int invoiceType) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("appid", YmlUtils.get("ly.360.appid"));
        paramMap.put("qid", YmlUtils.get("ly.360.qid"));
        paramMap.put("timestamp", String.format("%d", timestamp));
        paramMap.put("access_token", accessToken);
        paramMap.put("order_id", invoiceMap.get("orderid"));
        paramMap.put("invoice_title", invoiceMap.get("title"));
        paramMap.put("user_email", invoiceMap.get("email"));
        paramMap.put("tax_register_no", invoiceMap.get("taxno"));
        paramMap.put("address", invoiceMap.get("address"));
        paramMap.put("phone", invoiceMap.get("phone"));
        paramMap.put("bank_name", invoiceMap.get("bank"));
        paramMap.put("bank_account", invoiceMap.get("account"));
        paramMap.put("remarks", invoiceMap.get("remark"));
        if (invoiceType == 1){
            paramMap.put("custom_type", "1");
        }
        return paramMap;
    }

    private String build360Sign(Map<String, String> paramMap, String salt) {
        // 移除sign参数
        paramMap.remove("sign");
        
        // 过滤空值并收集键
        List<String> keys = new ArrayList<>();
        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                keys.add(entry.getKey());
            }
        }
        
        // 按键排序
        Collections.sort(keys);
        
        // 构建参数字符串
        StringBuilder paramStrBuilder = new StringBuilder();
        for (String key : keys) {
            paramStrBuilder.append(key)
                          .append("=")
                          .append(paramMap.get(key))
                          .append("&");
        }
        
        // 移除最后的&并添加salt
        String paramStr = paramStrBuilder.toString();
        if (paramStr.endsWith("&")) {
            paramStr = paramStr.substring(0, paramStr.length() - 1);
        }
        paramStr += salt;
        
        // 计算MD5
        String md5Str = ToolUtils.makeMd5(paramStr);
        return md5Str;
    }


    //获取360的发票请求信息
    public JSONObject invoice(Map<String, String> dataMap){
        String appid = YmlUtils.get("ly.360.appid");
        long qid = Long.parseLong(YmlUtils.get("ly.360.qid"));
        String appsecret = YmlUtils.get("ly.360.secret");
        //先申请授权
        String accessUrl = "http://api.openstore.360.cn/main/open/v1/auth/access_token";
        Map<String, Object> authParam = new HashMap<>();
        authParam.put("appid", appid);
        authParam.put("qid", qid);
        authParam.put("appsecret", appsecret);
        authParam.put("timestamp", System.currentTimeMillis() / 1000);
        String result = HttpUtils.sendPost(accessUrl, JSONObject.toJSONString(authParam));
        JSONObject jsonResult = new JSONObject();
        jsonResult = JSON.parseObject(result);
        if (StringUtils.isNull(jsonResult) || Integer.parseInt(jsonResult.get("errno").toString()) != 0) {
            throw new OperateException("获取360发票access_token失败");
        }
        JSONObject dataObj = jsonResult.getJSONObject("data");
        if (StringUtils.isNull(dataObj)) {
            throw new OperateException("获取360发票access_token data失败");
        }
        long timestamp = System.currentTimeMillis() / 1000;
        String accessToken = dataObj.getString("access_token");
        Map<String, String> paramMap = transferInvoice(dataMap, timestamp, accessToken, Integer.parseInt(dataMap.get("type").toString()));
        String sign = build360Sign(paramMap, appsecret);
        JSONObject params = new JSONObject();
        params.put("appid", YmlUtils.get("ly.360.appid"));
        params.put("qid", qid);
        params.put("timestamp", timestamp);
        params.put("access_token", accessToken);
        params.put("sign", sign);
        params.put("order_id", dataMap.get("orderid").toString());
        params.put("invoice_title", dataMap.get("title").toString());
        params.put("user_email", dataMap.get("email").toString());
        params.put("tax_register_no", dataMap.get("taxno").toString());
        params.put("address", dataMap.get("address").toString());
        params.put("phone", dataMap.get("phone").toString());
        params.put("bank_name", dataMap.get("bank").toString());
        params.put("bank_account", dataMap.get("account").toString());
        params.put("remarks", dataMap.get("remark").toString());
        Integer invoiceType = Integer.parseInt(dataMap.get("type").toString());
        String queryUrl = "http://api.openstore.360.cn/main/gateway/v1/order/invoicing";
        if (invoiceType == 1){//申请专票
            params.put("custom_type", "1"); 
            queryUrl = "http://api.openstore.360.cn/main/gateway/v1/invoice/dospecial";
        }
        result = HttpUtils.sendPost(queryUrl, params.toJSONString());
        return JSON.parseObject(result);
    }

    public JSONObject querySpecial(String sourceId, String rType){
        String appid = YmlUtils.get("ly.360.appid");
        long qid = Long.parseLong(YmlUtils.get("ly.360.qid"));
        String appsecret = YmlUtils.get("ly.360.secret");
        //先申请授权
        String accessUrl = "http://api.openstore.360.cn/main/open/v1/auth/access_token";
        Map<String, Object> authParam = new HashMap<>();
        authParam.put("appid", appid);
        authParam.put("qid", qid);
        authParam.put("appsecret", appsecret);
        authParam.put("timestamp", System.currentTimeMillis() / 1000);
        String result = HttpUtils.sendPost(accessUrl, JSONObject.toJSONString(authParam));
        JSONObject jsonResult = new JSONObject();
        jsonResult = JSON.parseObject(result);
        if (StringUtils.isNull(jsonResult) || Integer.parseInt(jsonResult.get("errno").toString()) != 0) {
            throw new OperateException("查询360专票access_token失败");
        }
        JSONObject dataObj = jsonResult.getJSONObject("data");
        if (StringUtils.isNull(dataObj)) {
            throw new OperateException("查询360专票access_token data失败");
        }
        String accessToken = dataObj.getString("access_token");
        Long timestamp = System.currentTimeMillis() / 1000;
        Map<String, String> param = new HashMap<>();
        param.put("appid", appid);
        param.put("qid", String.format("%d", qid));
        param.put("access_token", accessToken);
        param.put("timestamp", String.format("%d", timestamp));
        param.put("source_id", sourceId);
        param.put("request_type", rType);
        String sign = build360Sign(param, appsecret);
        JSONObject queryParams = new JSONObject();
        queryParams.put("appid", appid);
        queryParams.put("qid",  qid);
        queryParams.put("timestamp", timestamp);
        queryParams.put("access_token", accessToken);
        queryParams.put("sign", sign);
        queryParams.put("source_id", sourceId);
        queryParams.put("request_type", rType);
        String queryUrl = "http://api.openstore.360.cn/main/gateway/v1/invoice/queryspecial";
        result = HttpUtils.sendPost(queryUrl, queryParams.toJSONString());
        return JSON.parseObject(result);
    }
}
