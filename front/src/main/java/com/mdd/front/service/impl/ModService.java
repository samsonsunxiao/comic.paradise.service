package com.mdd.front.service.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.CopyObjectRequest;
import com.aliyun.oss.model.CopyObjectResult;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.mod.XMod;
import com.mdd.common.entity.mod.XModFiles;
import com.mdd.common.entity.mod.XModImages;
import com.mdd.common.entity.user.UserVip;
import com.mdd.common.entity.vip.LevelRights;
import com.mdd.common.entity.vip.LevelRightsVo;
import com.mdd.common.exception.OperateException;
import com.mdd.common.mapper.xmod.XModImagesMapper;
import com.mdd.common.mapper.user.UserVipMapper;
import com.mdd.common.mapper.vip.LevelRightsMapper;
import com.mdd.common.mapper.xmod.XModFilesMapper;
import com.mdd.common.mapper.xmod.XModMapper;
import com.mdd.common.util.AliyunCDNAuth;
import com.mdd.common.util.ConfigUtils;
import com.mdd.common.util.MapUtils;
import com.mdd.common.util.StringUtils;
import com.mdd.common.util.TimeUtils;
import com.mdd.common.util.YmlUtils;
import com.mdd.front.service.IModService;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.vo.xmod.ModDetailVo;
import com.mdd.front.vo.xmod.ModDownloadVo;
import com.mdd.front.vo.xmod.ModSummaryVo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ModService implements IModService {

    @Autowired
    @Resource
    XModMapper modMapper;

    @Autowired
    @Resource
    XModImagesMapper modImagesMapper;

    @Autowired
    @Resource
    XModFilesMapper modFilesMapper;

    @Resource
    UserVipMapper userVipMapper;

    @Resource
    LevelRightsMapper levelRightsMapper;

    @Override
    public ModDetailVo detail(String modid) {
        XMod model = modMapper.selectOne(
                new QueryWrapper<XMod>()
                        .eq("modid", modid));
        Assert.notNull(model, "MOD不存在");
        ModDetailVo vo = new ModDetailVo();
        BeanUtils.copyProperties(model, vo);
        XModImages modImage = modImagesMapper
                .selectOne(new QueryWrapper<XModImages>().eq("uid", model.getModid()).last("limit 1"));
        if (modImage != null) {
            vo.setImage(modImage.getUrl());
        }
        return vo;
    }

    @Override
    public PageResult<ModSummaryVo> listStore(PageValidate pageValidate, String searchParam) {
        Map<String, String> mapParam = MapUtils.jsonToMap(searchParam);
        Integer pageNo = pageValidate.getPage_no();
        Integer pageSize = pageValidate.getPage_size();
        MPJQueryWrapper<XMod> mpjQueryWrapper = new MPJQueryWrapper<XMod>()
                .select("MIN(t.id) as id, MIN(t.title) as name, t.modid as modid, MIN(t.vip) as vip, MIN(t.hot) as hot, MIN(t.downloadcount) as downloadcount, MIN(t.online_time) as onlineTime, MIN(img.url) as image, MIN(file.keyuri) as packageUrl, MIN(file.size) as size")
                .innerJoin("xmod_images img ON t.modid=img.uid and img.status='online'")
                .innerJoin("xmod_files file ON t.modid=file.uid and file.type='combin' and file.status='online'")
                .groupBy("t.modid")
                .eq("t.status", "online");

        if (mapParam.get("key").equals("hot")) {
            if (mapParam.get("sort").equals("desc")) {
                mpjQueryWrapper.orderByDesc("hot");
            } else {
                mpjQueryWrapper.orderByAsc("hot");
            }
        } else if (mapParam.get("key").equals("date")) {
            if (mapParam.get("sort").equals("desc")) {
                mpjQueryWrapper.orderByDesc("online_time");
            } else {
                mpjQueryWrapper.orderByAsc("online_time");
            }
        } else if (mapParam.get("key").equals("downloadcount")) {
            if (mapParam.get("sort").equals("desc")) {
                mpjQueryWrapper.orderByDesc("downloadcount");
            } else {
                mpjQueryWrapper.orderByAsc("downloadcount");
            }
        }
        if (!mapParam.get("keyword").isEmpty()) {
            mpjQueryWrapper.nested(wq -> wq.like("t.title", mapParam.get("keyword")));
        }
        IPage<ModSummaryVo> iPage = modMapper.selectJoinPage(
                new Page<>(pageNo, pageSize),
                ModSummaryVo.class,
                mpjQueryWrapper);
        // Map<String, String> websiteConfig = ConfigUtils.get("website");
        // String strAliCdn = websiteConfig.getOrDefault("aliCdn", "");
        for (ModSummaryVo item : iPage.getRecords()) {
            // if (!item.getPackageUrl().isEmpty()){
            // String packageKeyUri = item.getPackageUrl();
            // try {
            // // Extract the filename from the URL
            // String filename = packageKeyUri.substring(packageKeyUri.lastIndexOf('/') +
            // 1);
            // // Encode the filename
            // String encodedFilename = URLEncoder.encode(filename, "UTF-8");
            // // Replace the original filename with the encoded filename
            // packageKeyUri = packageKeyUri.substring(0, packageKeyUri.lastIndexOf('/') +
            // 1) + encodedFilename;
            // } catch (UnsupportedEncodingException e) {
            // throw new OperateException(e.getMessage());
            // }
            // String packageUrl = strAliCdn + "/" + packageKeyUri;
            // item.setPackageUrl(packageUrl);
            // }
            if (!item.getOnlineTime().isEmpty()) {
                item.setOnlineTime(TimeUtils.timestampToDate(item.getOnlineTime(), "yyyy-MM-dd"));
            }
        }
        return PageResult.iPageHandle(iPage);
    }

    public static String generalTemplateFile(String modid, String objectName, Integer userId) {
        Map<String, String> storageConfig = ConfigUtils.get("storage");
        Map<String, String> aliyunConfig = MapUtils.jsonToMap(storageConfig.getOrDefault("aliyun", ""));
        String strAliBucket = aliyunConfig.getOrDefault("bucket", "");
        String accessKeyId = YmlUtils.get("ali-oss.accessKeyId");
        String accessKeySecret = YmlUtils.get("ali-oss.accessKeySecret");
        String endpoint = YmlUtils.get("ali-oss.endpoint");
        // 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ObjectMetadata metadata = ossClient.headObject(strAliBucket, objectName);
        if (metadata == null) {
            log.error("generalTemplateFile aliyun oss can not find objectName:{}", objectName);
            return "";
        }
        String fileName = objectName.substring(objectName.lastIndexOf('/') + 1);
        long timestamp = System.currentTimeMillis();
        // 精确到毫秒,做清理工作时，需要注意
        String strNewKeyid = "resources/template/" + modid + "/" + userId + "/" + timestamp + "/" + fileName;
        CopyObjectRequest copyObjectRequest = new CopyObjectRequest(
                strAliBucket, objectName, strAliBucket, strNewKeyid);
        try {
            CopyObjectResult result = ossClient.copyObject(copyObjectRequest);
            // 打印拷贝结果
            System.out.println("ETag: " + result.getETag());
            System.out.println("Last Modified: " + result.getLastModified());
            System.out.println("Copy completed successfully!");
            try {
                // Encode the filename
                fileName = URLEncoder.encode(fileName, "UTF-8");
                strNewKeyid = "resources/template/" + modid + "/" + userId + "/" + timestamp + "/" + fileName;
            } catch (UnsupportedEncodingException e) {
                throw new OperateException(e.getMessage());
            }
            return strNewKeyid;
        } catch (OSSException oe) {
            System.out.println("Error Message: " + oe.getErrorMessage());
            System.out.println("Error Code: " + oe.getErrorCode());
            System.out.println("Request ID: " + oe.getRequestId());
            System.out.println("Host ID: " + oe.getHostId());
        } finally {
            // 关闭OSSClient
            ossClient.shutdown();
        }
        return null;
    }

    private void RefreshVipData(UserVip userVip, long curTime) {
        // 最后下载时间，过了一天就刷新,
        long lastDownloadTime = userVip.getLasttime();
        // 转换时间戳为 LocalDate
        LocalDate date1 = Instant.ofEpochSecond(lastDownloadTime)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        LocalDate date2 = Instant.ofEpochSecond(curTime)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        // 计算天数差
        long daysBetween = ChronoUnit.DAYS.between(date1, date2);
        if (daysBetween < 1) {// 不够一天就不用刷新
            return;
        }
        MPJQueryWrapper<LevelRights> mpjQueryWrapper = new MPJQueryWrapper<LevelRights>()
                .selectAll(LevelRights.class)
                .select("r.value as rightvalue,r.type as righttype")
                .innerJoin("xmod_vip_rights r ON r.keyid=t.rights")
                .innerJoin("xmod_vip_level l ON l.keyid=t.level")
                .eq("l.value", userVip.getVip());
        IPage<LevelRightsVo> iPage = levelRightsMapper.selectJoinPage(
                new Page<>(0, -1),
                LevelRightsVo.class,
                mpjQueryWrapper);
        List<Map<String, BigInteger>> listRights = new LinkedList<>();
        for (LevelRightsVo item1 : iPage.getRecords()) {
            Map<String, BigInteger> map = new HashMap<>();
            // 流量G转为字节
            if (item1.getRighttype().equals("traffic")) {
                BigInteger traffic = BigInteger.valueOf(item1.getRightvalue()).multiply(BigInteger.valueOf(1024))
                        .multiply(BigInteger.valueOf(1024))
                        .multiply(BigInteger.valueOf(1024));
                map.put(item1.getRighttype(), traffic);
            } else {
                map.put(item1.getRighttype(), BigInteger.valueOf(item1.getRightvalue()));
            }
            listRights.add(map);
        }

        JSONArray dataArray = JSONArray.parseArray(userVip.getData());
        for (int i = 0; i < dataArray.size(); i++) {
            JSONObject dataJson = dataArray.getJSONObject(i);
            if (StringUtils.isNotNull(dataJson.getInteger("count"))) {
                Map<String, BigInteger> result = listRights.stream()
                        .filter(map -> map.containsKey("count"))
                        .findFirst()
                        .orElse(null);
                dataJson.put("count", result.get("count"));
            } else if (StringUtils.isNotNull(dataJson.getBigInteger("traffic"))) {
                Map<String, BigInteger> result = listRights.stream()
                        .filter(map -> map.containsKey("traffic"))
                        .findFirst()
                        .orElse(null);
                dataJson.put("traffic", result.get("traffic"));
            }
            dataArray.set(i, dataJson);
        }
        userVip.setData(dataArray.toJSONString());

    }

    @Override
    public ModDownloadVo queryDownload(String modid, String supply, String version, Integer terminal, Integer userId) {
        // 先校验MOD是否需要VIP
        XMod model = modMapper.selectOne(
                new QueryWrapper<XMod>()
                        .eq("modid", modid));
        Assert.notNull(model, "MOD不存在");
        Map<String, String> storageConfig = ConfigUtils.get("storage");
        Map<String, String> aliyunConfig = MapUtils.jsonToMap(storageConfig.getOrDefault("aliyun", ""));
        String strAliCdn = aliyunConfig.getOrDefault("domain", "");
        if (supply.isEmpty()){
            strAliCdn = "https://cdn.x-mod.cn";
        }
        ModDownloadVo vo = new ModDownloadVo();
        String packageUrl = "";
        BigInteger speed = BigInteger.valueOf(100);
        if (model.getVip()) {
            if (userId != 0) {
                // 需要VIP
                // 校验User是否VIP
                UserVip userVip = userVipMapper.selectOne(new QueryWrapper<UserVip>()
                        .eq("user_id", userId)
                        .last("limit 1"));
                Assert.notNull(userVip, "用户VIP信息不存在");
                // 超时判断，精确到S
                long expireTime = userVip.getExpireTime();
                long curTime = System.currentTimeMillis() / 1000;
                if (expireTime < curTime) {// 超时了
                    vo.setCode(-2);
                    return vo;
                }
                // 刷新权益
                RefreshVipData(userVip, curTime);
                // 权益校验
                JSONArray dataArray = JSONArray.parseArray(userVip.getData());
                for (int i = 0; i < dataArray.size(); i++) {
                    JSONObject dataJson = dataArray.getJSONObject(i);
                    if (StringUtils.isNotNull(dataJson.getInteger("count"))) {// 检验次数，每次下载消耗一次
                        Integer count = dataJson.getInteger("count");
                        if (count <= 0) {
                            vo.setCode(-3);
                            return vo;
                        } else {
                            dataJson.put("count", count - 1);
                            dataArray.set(i, dataJson);
                        }
                    } else if (StringUtils.isNotNull(dataJson.getBigInteger("traffic"))) {// 检验流量，每次下载消耗流量
                        XModFiles modFile = modFilesMapper.selectOne(
                                new QueryWrapper<XModFiles>().eq("uid", model.getModid()).eq("type", "combin")
                                        .eq("status", "online").last("limit 1"));
                        if (modFile != null) {
                            BigInteger traffic = dataJson.getBigInteger("traffic");
                            if (traffic.compareTo(BigInteger.valueOf(modFile.getSize())) < 0) {
                                vo.setCode(-4);
                                return vo;
                            } else {
                                dataJson.put("traffic", traffic.subtract(BigInteger.valueOf(modFile.getSize())));
                                dataArray.set(i, dataJson);
                            }
                        } else {
                            vo.setCode(-2);
                            return vo;
                        }
                    }else if(StringUtils.isNotNull(dataJson.getBigInteger("speed"))){
                        // 速度限制
                        speed = dataJson.getBigInteger("speed");
                    }
                }
                userVip.setLasttime(curTime);
                userVip.setData(dataArray.toJSONString());
                userVipMapper.updateById(userVip);
            } else {
                vo.setCode(-1);
                return vo;
            }
        }
        vo.setCode(1);
        // 生成下载链接
        XModFiles modFile = modFilesMapper.selectOne(
                new QueryWrapper<XModFiles>().eq("uid", model.getModid()).eq("type", "combin")
                        .eq("status", "online").last("limit 1"));
        Assert.notNull(modFile, "文件不存在");
        if (modFile.getKeyuri().isEmpty()) {
            log.error("generalTemplateFile mod: " + model.getModid() + " name:" + model.getTitle()
                    + " failed keyuri is empty");
            vo.setCode(-5);
            return vo;
        }
        // OSS生成一个临时文件
        String newKeyuri = generalTemplateFile(model.getModid(), modFile.getKeyuri(), userId);
        if (newKeyuri == null) {
            log.error("generalTemplateFile failed newKeyuri is null");
            vo.setCode(-6);
            return vo;
        }
        XModFiles modNewFile = new XModFiles();
        // 写入临时
        BeanUtils.copyProperties(modFile, modNewFile);
        modNewFile.setId(0);
        modNewFile.setKeyuri(newKeyuri);
        modNewFile.setType("temp");
        modFilesMapper.insert(modNewFile);
        packageUrl = strAliCdn + "/" + newKeyuri;
        if (!supply.isEmpty()){//表示新的支持CDN各种访问限制限速的
             //生成CDN鉴权链接
            packageUrl = AliyunCDNAuth.generateAuthUrl(strAliCdn , "/" + newKeyuri, "app.x-mod.cn", 3600);
            packageUrl += "&speed=" + speed;
        }
        vo.setCode(1);
        vo.setSize(modFile.getSize());
        vo.setPackageUrl(packageUrl);
        vo.setModid(modid);
        vo.setName(model.getTitle());
        return vo;
    }
}
