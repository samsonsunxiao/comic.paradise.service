package com.mdd.admin.controller.aliyun;

import com.mdd.admin.aop.Log;
import com.mdd.common.aliyun.AliOSSService;
import com.mdd.common.aop.NotLogin;
import com.mdd.common.aop.NotPower;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.util.ConfigUtils;
import com.mdd.common.util.MapUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("adminapi/aliyun")
@Api(tags = "阿里云操作接口")
public class AliyunController {

    @Resource
    AliOSSService aliOSSService;

    @NotLogin
    @NotPower
    @Log(title = "获取sts")
    @GetMapping("/sts")
    @ApiOperation(value = "获取sts")
    public AjaxResult<Object> getsts() {
        Map<String, Object> res = aliOSSService.getSts();
        Map<String, String> storageConfig = ConfigUtils.get("storage");
        Map<String, String> aliyunConfig = MapUtils.jsonToMap(storageConfig.getOrDefault("aliyun", ""));
        Map<String, String> config = MapUtils.jsonToMap(aliyunConfig.getOrDefault("config", ""));
        String strAliPath = config.getOrDefault("path", "");
        String strAliBucket = aliyunConfig.getOrDefault("bucket", "");
        String strAliRegion = aliyunConfig.getOrDefault("region", "");
        String strAliCdn = aliyunConfig.getOrDefault("domain", "");
        res.put("path", strAliPath);
        res.put("bucket", strAliBucket);
        res.put("region", strAliRegion);
        res.put("cdn", strAliCdn);
        return AjaxResult.success(res);
    }


}
