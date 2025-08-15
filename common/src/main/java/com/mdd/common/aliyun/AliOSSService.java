// This file is auto-generated, don't edit it. Thanks.
package com.mdd.common.aliyun;


import lombok.extern.slf4j.Slf4j;

import org.apache.ibatis.util.MapUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mdd.common.util.ConfigUtils;
import com.mdd.common.util.MapUtils;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AliOSSService {

    @Value("${ali-oss.accessKeySecret}")
    private String accessKeySecret;

    @Value("${ali-oss.accessKeyId}")
    private String accessKeyId;

    @Value("${ali-oss.roleArn}")
    private String roleArn;

    @Value("${ali-oss.roleSessionName}")
    private String roleSessionName;

    @Value("${ali-oss.durationSeconds}")
    private Long durationSeconds;

    // @Value("${ali-oss.policy}")
    // private String policy;

    @Value("${ali-oss.endpoint}")
    private String endpoint;

    /**
     * 使用AK&SK初始化账号Client
     *
     * @param accessKeyId
     * @param accessKeySecret
     * @return Client
     * @throws Exception
     */
    public com.aliyun.teaopenapi.Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                // 必填，您的 AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 必填，您的 AccessKey Secret
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        Map<String, String> storageConfig = ConfigUtils.get("storage");
        Map<String, String> aliyunConfig = MapUtils.jsonToMap(storageConfig.getOrDefault("aliyun", ""));
        Map<String, String> configCloud = MapUtils.jsonToMap(aliyunConfig.getOrDefault("config", ""));
        String strAliEndpoint = configCloud.getOrDefault("sts", "");
        config.endpoint = strAliEndpoint;
        return new com.aliyun.teaopenapi.Client(config);
    }

    /**
     * 使用STS鉴权方式初始化账号Client，推荐此方式。
     *
     * @param accessKeyId
     * @param accessKeySecret
     * @param securityToken
     * @return Client
     * @throws Exception
     */
    public com.aliyun.teaopenapi.Client createClientWithSTS(String accessKeyId, String accessKeySecret, String securityToken) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                // 必填，您的 AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 必填，您的 AccessKey Secret
                .setAccessKeySecret(accessKeySecret)
                // 必填，您的 Security Token
                .setSecurityToken(securityToken)
                // 必填，表明使用 STS 方式
                .setType("sts");
        // 访问的域名
        //config.endpoint = "sts.cn-shenzhen.aliyuncs.com";
        Map<String, String> websiteConfig = ConfigUtils.get("website");
        String strAliEndpoint = websiteConfig.getOrDefault("aliEndpoint", "");
        config.endpoint = strAliEndpoint;

        return new com.aliyun.teaopenapi.Client(config);
    }

    /**
     * API 相关
     *
     * @return OpenApi.Params
     */
    public static com.aliyun.teaopenapi.models.Params createApiInfo() throws Exception {
        com.aliyun.teaopenapi.models.Params params = new com.aliyun.teaopenapi.models.Params()
                // 接口名称
                .setAction("AssumeRole")
                // 接口版本
                .setVersion("2015-04-01")
                // 接口协议
                .setProtocol("HTTPS")
                // 接口 HTTP 方法
                .setMethod("POST")
                .setAuthType("AK")
                .setStyle("RPC")
                // 接口 PATH
                .setPathname("/")
                // 接口请求体内容格式
                .setReqBodyType("json")
                // 接口响应体内容格式
                .setBodyType("json");
        return params;
    }

    public Map<String, Object> getSts()  {
        String exceptionStr = "";
        try {
            // 请确保代码运行环境设置了环境变量 ALIBABA_CLOUD_ACCESS_KEY_ID 和 ALIBABA_CLOUD_ACCESS_KEY_SECRET。
            // 工程代码泄露可能会导致 AccessKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议使用更安全的 STS 方式，更多鉴权访问方式请参见：https://help.aliyun.com/document_detail/378657.html
            Map<String, String> storageConfig = ConfigUtils.get("storage");
            Map<String, String> aliyunConfig = MapUtils.jsonToMap(storageConfig.getOrDefault("aliyun", ""));
            Map<String, String> config = MapUtils.jsonToMap(aliyunConfig.getOrDefault("config", ""));
            String strPolicy = config.getOrDefault("policy", "");
            com.aliyun.teaopenapi.Client client = createClient(accessKeyId, accessKeySecret);
            com.aliyun.teaopenapi.models.Params params = AliOSSService.createApiInfo();
            // query params
            Map<String, Object> queries = new HashMap<>();
            
            queries.put("DurationSeconds", durationSeconds);
            queries.put("Policy", strPolicy);
            queries.put("RoleArn", roleArn);
            queries.put("RoleSessionName", roleSessionName);
            log.info("oss.call.req:"+com.aliyun.teautil.Common.toJSONString(queries));

            // runtime options
            com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
            com.aliyun.teaopenapi.models.OpenApiRequest request = new com.aliyun.teaopenapi.models.OpenApiRequest()
                    .setQuery(com.aliyun.openapiutil.Client.query(queries));
            // 复制代码运行请自行打印 API 的返回值
            // 返回值为 Map 类型，可从 Map 中获得三类数据：响应体 body、响应头 headers、HTTP 返回的状态码 statusCode。
            Map<String, Object> resp = (Map<String, Object>) client.callApi(params, request, runtime);
            log.info("callApi.resp:" + com.aliyun.teautil.Common.toJSONString(resp));
            if (((Integer) resp.get("statusCode")) == 200) {
                Map<String, Object> bodyMap = (Map<String, Object>) resp.get("body");
                return (Map<String, Object>) bodyMap.get("Credentials");
            }
        } catch (Exception e) {
            log.error("sts.error",e);
            exceptionStr = e.toString();
        }
        Map<String, Object> res = new HashMap();
        res.put("sts.error",exceptionStr);

        return res;
    }


}
