package com.mdd.admin.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.admin.service.IModService;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.mod.XModSaveValidate;
import com.mdd.admin.validate.mod.XModSearchValidate;
import com.mdd.common.core.PageResult;
import com.mdd.common.mapper.xmod.XModFilesMapper;
import com.mdd.common.mapper.xmod.XModImagesMapper;
import com.mdd.common.mapper.xmod.XModTempMapper;
import com.mdd.common.mapper.xmod.XModMapper;
import com.mdd.common.util.ConfigUtils;
import com.mdd.common.util.MapUtils;
import com.mdd.common.util.MultiFileDownloader;
import com.mdd.common.util.RedisUtils;
import com.mdd.common.util.TimeUtils;
import com.mdd.common.util.YmlUtils;

import com.mdd.admin.vo.mod.XModListedVo;
import com.mdd.admin.vo.mod.XModOfflineVo;
import com.mdd.admin.vo.mod.XModDetailVo;
import com.mdd.common.entity.mod.XModFiles;
import com.mdd.common.entity.mod.XModImages;
import com.mdd.common.entity.mod.XModTemp;
import com.mdd.common.exception.OperateException;
import com.mdd.common.entity.mod.XMod;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import javax.annotation.Resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * MOD服务实现类
 */
@Slf4j
@Service
public class ModServiceImpl implements IModService {

    @Resource
    @Autowired
    XModMapper resMapper;

    @Resource
    @Autowired
    XModFilesMapper modFilesMapper;

    @Resource
    @Autowired
    XModImagesMapper modImagesMapper;

    @Resource
    XModTempMapper modTempMapper;

    @Autowired
    private RedisUtils redisLockUtil;

    /**
     * 文章列表
     *
     * @param pageValidate   分页参数
     * @param searchValidate 搜索参数
     * @return PageResult<ResourceListedVo>
     * @author fzr
     */
    @Override
    public PageResult<XModListedVo> list(PageValidate pageValidate, XModSearchValidate searchValidate) {
        Integer pageNo = pageValidate.getPage_no();
        Integer pageSize = pageValidate.getPage_size();
        MPJQueryWrapper<XMod> mpjQueryWrapper = new MPJQueryWrapper<XMod>()
                .selectAll(XMod.class)
                // .select("mf.url as file")
                // .innerJoin("xmod_files mf ON mf.uid=t.modid and mf.type='normal'")
                .orderByDesc("t.date");
        String text = searchValidate.getText();
        mpjQueryWrapper.nested(wq -> wq.like("t.title", text).or().like("t.descript", text));
        if (!searchValidate.getStatus().isEmpty()) {
            mpjQueryWrapper.nested(wq -> wq.eq("t.status", searchValidate.getStatus()));
        }
        resMapper.setSearch(mpjQueryWrapper, searchValidate, new String[] {
                "=:platform@t.platform:str",
                "=:gid@t.gid:str"
        });
        IPage<XMod> iPage = resMapper.selectJoinPage(
                new Page<>(pageNo, pageSize),
                XMod.class,
                mpjQueryWrapper);
        List<XModListedVo> listMod = new LinkedList<>();
        for (XMod item : iPage.getRecords()) {
            XModListedVo vo = new XModListedVo();
            BeanUtils.copyProperties(item, vo);
            XModImages modImage = modImagesMapper
                    .selectOne(new QueryWrapper<XModImages>().eq("uid", item.getModid()).last("limit 1"));
            if (modImage != null) {
                vo.setThumbImage(modImage.getUrl());
            }
            listMod.add(vo);
        }
        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), listMod);
    }

    /**
     * 资源详情
     *
     * @param id 主键ID
     * @author fzr
     */
    @Override
    public XModDetailVo detail(String modid) {
        XMod model = resMapper.selectOne(
                new QueryWrapper<XMod>()
                        .eq("modid", modid));
        Assert.notNull(model, "mod不存在");
        String gid = model.getGid();
        // 获取包列表
        List<XModFiles> listFile = modFilesMapper
                .selectList(new QueryWrapper<XModFiles>().eq("uid", modid).eq("type", "normal").ne("status", "delete"));
        // 获取图片列表
        List<XModImages> listImage = modImagesMapper
                .selectList(new QueryWrapper<XModImages>().eq("uid", modid).ne("status", "delete"));
        List<XModFiles> listManagers = modFilesMapper
                .selectList(new QueryWrapper<XModFiles>().eq("uid", gid).eq("status", "online"));

        List<Map<String, Object>> listMapManagers = new LinkedList<>();
        Map<String, String> websiteConfig = ConfigUtils.get("website");
        for (XModFiles item : listManagers) {
            Map<String, Object> mapManagers = new HashMap<>();
            String straliCdn = websiteConfig.getOrDefault("aliCdn", "");
            String packageUrl = straliCdn + "/" + item.getKeyuri();
            mapManagers.put("package_url", packageUrl);
            mapManagers.put("size", item.getSize());
            mapManagers.put("descript", item.getDescript());
            listMapManagers.add(mapManagers);
        }
        XModFiles combinPackage = modFilesMapper.selectOne(
                new QueryWrapper<XModFiles>()
                        .eq("uid", modid)
                        .eq("type", "combin")
                        .eq("status", "online"));

        XModDetailVo vo = new XModDetailVo();
        BeanUtils.copyProperties(model, vo);
        vo.setFiles(listFile);
        vo.setImages(listImage);
        vo.setCombinPackage(combinPackage);
        vo.setManagers(listMapManagers);
        if (model.getOnlineTime() == 0) {
            vo.setDate("");
        } else {
            vo.setDate(TimeUtils.timestampToDate(model.getOnlineTime()));
        }
        return vo;
    }

    public void save(XModSaveValidate saveValidate) {
        if (saveValidate.getModid().isEmpty()) {
            Random random = new Random();
            int modid = 100000 + random.nextInt(900000); // 生成100000到999999之间的随机数
            saveValidate.setModid("MN" + modid);
        }
        XMod model = resMapper.selectOne(
                new QueryWrapper<XMod>()
                        .eq("modid", saveValidate.getModid()));
        Boolean isNew = false;
        if (model == null) {
            model = new XMod();
            model.setModid(saveValidate.getModid());
            if (saveValidate.getStatus().isEmpty()) {
                saveValidate.setStatus("offline");
            }
            isNew = true;
        }
        model.setTitle(saveValidate.getTitle());
        model.setStatus(saveValidate.getStatus());
        model.setInstalldesc(saveValidate.getInstalldesc());
        model.setDescript(saveValidate.getDescript());
        model.setGid(saveValidate.getGid());
        model.setGame(saveValidate.getGame());
        model.setVip(saveValidate.getVip());
        if (saveValidate.getDate().isEmpty()) {
            model.setOnlineTime(System.currentTimeMillis() / 1000);
        } else {
            model.setOnlineTime(TimeUtils.dateToTimestamp(saveValidate.getDate()));
        }
        if (isNew) {
            resMapper.insert(model);
        } else {
            resMapper.updateById(model);
        }
        XModFiles modFile = saveValidate.getCombinPackage();
        if (modFile != null) {
            XModFiles combinOld = modFilesMapper.selectOne(
                    new QueryWrapper<XModFiles>()
                            .eq("uid", saveValidate.getModid())
                            .eq("type", "combin")
                            .eq("status", "online"));
            if (combinOld != null) {
                combinOld.setFilename(modFile.getFilename());
                combinOld.setKeyuri(modFile.getKeyuri());
                combinOld.setSize(modFile.getSize());
                combinOld.setUrl(modFile.getUrl());
                modFilesMapper.updateById(combinOld);
            } else {
                modFile.setStatus("online");
                modFile.setType("combin");
                modFile.setUid(saveValidate.getModid());
                modFilesMapper.insert(modFile);
            }
        }

        // 处理图片
        processImages(saveValidate.getImages(), saveValidate.getModid());
        // processFiles(saveValidate.getFiles(), saveValidate.getModid());
    }

    @Autowired
    private ApplicationContext context;

    public void processImages(List<XModImages> listImagesR, String modid) {
        List<XModImages> listImages = modImagesMapper.selectList(new QueryWrapper<XModImages>().eq("uid", modid));

        List<XModImages> listDel = listImages.stream()
                .filter(image1 -> listImagesR.stream()
                        .noneMatch(image2 -> image1.getKeyuri().equals(image2.getKeyuri())))
                .collect(Collectors.toList());
        for (XModImages item : listImagesR) {
            item.setType("mod");
            if (item.getStatus().equals("local")) {
                item.setStatus("offline");
            }
            Optional<XModImages> result = listImages.stream()
                    .filter(image -> image.getKeyuri().equals(item.getKeyuri())) // 假设 XModImages 类中有 getId() 方法
                    .findFirst();
            XModImages findItem = result.orElse(null);
            if (findItem == null) {
                modImagesMapper.insert(item);
            } else {
                item.setId(findItem.getId());
                modImagesMapper.updateById(item);
            }
        }
        for (XModImages item : listDel) {
            item.setStatus("delete");
            modImagesMapper.updateById(item);
        }
        final List<XModImages> listImagesDl = listImagesR;
        String savePath = YmlUtils.get("app.upload-directory");
        File saveDir = new File(savePath);
        if (!saveDir.exists()) {
            if (!saveDir.mkdirs()) {
                throw new OperateException("创建下载目录失败");
            }
        }
        final String strSaveDir = savePath;
        // 异步把图片都上传到阿里云OSS中
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 线程池，控制并发的线程数量
                    ConcurrentHashMap<String, Boolean> uploadStatus = new ConcurrentHashMap<>();
                    ExecutorService executor = Executors.newFixedThreadPool(5); // 5 个线程并发
                    for (XModImages item : listImagesDl) {
                        if (uploadStatus.getOrDefault(item.getKeyuri(), false)) {
                            continue; // 另一个线程已经在处理该文件
                        }
                        uploadStatus.put(item.getKeyuri(), true);
                        String url = item.getUrl();
                        // 根据文件 URL 动态生成保存路径
                        String fileName = url.substring(url.lastIndexOf('/') + 1);
                        String saveFile = strSaveDir + fileName;
                        // 检测OSS是否存在。存在就不上传了
                        ObjectMetadata metadata = getFileMeta(item.getKeyuri());
                        if (metadata != null) {
                            XModImages modImage = modImagesMapper
                                    .selectOne(new QueryWrapper<XModImages>().eq("keyuri", item.getKeyuri()));
                            if (modImage != null) {
                                modImage.setSize(metadata.getContentLength());
                                modImage.setStatus("online");
                                modImagesMapper.updateById(modImage);
                            }
                            File file = new File(saveFile);
                            file.delete();
                            continue;
                        }
                        GameImageDownloadProcess listener = context.getBean(GameImageDownloadProcess.class,
                                item.getKeyuri());
                        // 提交下载任务
                        executor.execute(new MultiFileDownloader.DownloadTask(url, saveFile, listener));
                    }
                    // 关闭线程池并等待所有任务完成
                    executor.shutdown();
                    try {
                        if (!executor.awaitTermination(60, TimeUnit.MINUTES)) {
                            executor.shutdownNow();
                        }
                    } catch (InterruptedException e) {
                        executor.shutdownNow();
                    }
                    System.out.println("All image process completed!");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void processFiles(List<XModFiles> listFilesR, String modid) {
        List<XModFiles> listFiles = modFilesMapper.selectList(new QueryWrapper<XModFiles>().eq("uid", modid));

        List<XModFiles> listDel = listFiles.stream()
                .filter(file1 -> listFilesR.stream()
                        .noneMatch(file2 -> file1.getKeyuri().equals(file2.getKeyuri())))
                .collect(Collectors.toList());
        for (XModFiles item : listFilesR) {
            if (item.getStatus().equals("local")) {
                item.setStatus("offline");
            }
            Optional<XModFiles> result = listFiles.stream()
                    .filter(file -> file.getKeyuri().equals(item.getKeyuri())) // 假设 XModImages 类中有 getId() 方法
                    .findFirst();
            XModFiles findItem = result.orElse(null);
            if (findItem == null) {
                modFilesMapper.insert(item);
            } else {
                item.setId(findItem.getId());
                modFilesMapper.updateById(item);
            }
        }
        for (XModFiles item : listDel) {
            item.setStatus("delete");
            modFilesMapper.updateById(item);
        }
        final List<XModFiles> listFileDl = listFilesR;
        String savePath = YmlUtils.get("app.upload-directory");
        savePath = savePath + modid + "/original/";
        File saveDir = new File(savePath);
        if (!saveDir.exists()) {
            if (!saveDir.mkdirs()) {
                throw new OperateException("创建下载目录失败");
            }
        }
        log.info("processFiles savePath:" + savePath);
        final String strSaveDir = savePath;
        final String finalModid = modid;
        // 异步把图片都上传到阿里云OSS中
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 线程池，控制并发的线程数量
                    ConcurrentHashMap<String, Boolean> uploadStatus = new ConcurrentHashMap<>();
                    ExecutorService executor = Executors.newFixedThreadPool(5); // 5 个线程并发
                    List<String> listFiles = new LinkedList<>();
                    for (XModFiles item : listFileDl) {
                        if (uploadStatus.getOrDefault(item.getKeyuri(), false)) {
                            continue; // 另一个线程已经在处理该文件
                        }
                        uploadStatus.put(item.getKeyuri(), true);
                        String url = item.getUrl();
                        // 根据文件 URL 动态生成保存路径
                        String fileName = url.substring(url.lastIndexOf('/') + 1);
                        String saveFile = strSaveDir + fileName;
                        // 检测OSS是否存在。存在就不上传了
                        // ObjectMetadata metadata = getFileMeta(item.getKeyuri());
                        // if (metadata != null) {
                        // XModFiles modFile = modFilesMapper
                        // .selectOne(new QueryWrapper<XModFiles>().eq("keyuri", item.getKeyuri()));
                        // if (modFile != null) {
                        // modFile.setSize(metadata.getContentLength());
                        // modFile.setStatus("online");
                        // modFilesMapper.updateById(modFile);
                        // }
                        // File file = new File(saveFile);
                        // file.delete();
                        // continue;
                        // }
                        GameFileDownloadProcess listener = context.getBean(GameFileDownloadProcess.class,
                                item.getKeyuri());
                        // 提交下载任务
                        executor.execute(new MultiFileDownloader.DownloadTask(url, saveFile, listener));
                        listFiles.add(saveFile);
                    }
                    // 关闭线程池并等待所有任务完成
                    executor.shutdown();
                    try {
                        if (!executor.awaitTermination(60, TimeUnit.MINUTES)) {
                            executor.shutdownNow();
                        }
                    } catch (InterruptedException e) {
                        executor.shutdownNow();
                    }
                    log.info("All files process completed!");
                    String scriptPath = YmlUtils.get("app.script-path");
                    String pythonScriptPath1 = scriptPath + "modfile.sh";
                    String pythonScriptPath2 = scriptPath + "finalfile.sh";
                    String extractorDir = strSaveDir;
                    extractorDir = extractorDir + modid + "/target/";
                    for (String file : listFiles) {
                        CallPythonScript(pythonScriptPath1, finalModid, file, extractorDir);
                    }
                    String compressFile = YmlUtils.get("app.upload-directory");
                    compressFile = compressFile + modid + ".rar";
                    CallPythonScript(pythonScriptPath2, finalModid, extractorDir, compressFile);
                    log.info("All files compress final package completed! compressFile:" + compressFile);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void CallPythonScript(String scriptFile, String modid, String strDownloadFile, String extractorDir) {
        try {
            // 构建命令
            ProcessBuilder processBuilder = new ProcessBuilder("bash", scriptFile, strDownloadFile, extractorDir);

            // 启动进程
            Process process = processBuilder.start();

            // 获取 Python 脚本的输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            System.out.println("Shell Script Output:");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // 等待脚本执行完成
            int exitCode = process.waitFor();
            System.out.println("Shell Script exited with code: " + exitCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkKeyUriExisted(String keyuri) {
        return getFileMeta(keyuri) != null;
    }

    public static ObjectMetadata getFileMeta(String objectName) {
        // Map<String, String> websiteConfig = ConfigUtils.get("website");
        Map<String, String> storageConfig = ConfigUtils.get("storage");
        Map<String, String> aliyunConfig = MapUtils.jsonToMap(storageConfig.getOrDefault("aliyun", ""));
        String strAliBucket = aliyunConfig.getOrDefault("bucket", "");
        String accessKeyId = YmlUtils.get("ali-oss.accessKeyId");
        String accessKeySecret = YmlUtils.get("ali-oss.accessKeySecret");
        String endpoint = YmlUtils.get("ali-oss.endpoint");
        // 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            ObjectMetadata metadata = ossClient.headObject(strAliBucket, objectName);
            return metadata;
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

    @Override
    public XModOfflineVo getOneOffline() {
        MPJQueryWrapper<XMod> mpjQueryWrapper = new MPJQueryWrapper<XMod>()
                .selectAll(XMod.class)
                .select("mf.url as orgurl,mf.keyuri as keyuri, t.title as name, t.modid as modid")
                .innerJoin("xmod_files mf ON mf.uid=t.modid")
                .isNotNull("mf.keyuri").isNotNull("mf.url");
        IPage<XModOfflineVo> iPage = resMapper.selectJoinPage(
                new Page<>(0, -1),
                XModOfflineVo.class,
                mpjQueryWrapper);
        for (XModOfflineVo item : iPage.getRecords()) {
            XModTemp modTemp = modTempMapper.selectOne(new QueryWrapper<XModTemp>().eq("modid", item.getModid()));
            if (modTemp != null) {
                continue;
            }
            String lockKey = "lock:modid:" + item.getModid();
            String lockValue = UUID.randomUUID().toString();
            // 尝试获取锁
            if (!redisLockUtil.tryLock(lockKey, lockValue, 5)) {
                continue; // 获取锁失败，跳过
            }
            try {
                // 检查是否已经上传到OSS
                if (!checkKeyUriExisted(item.getKeyuri())) {
                    modTemp = new XModTemp();
                    modTemp.setModid(item.getModid());
                    modTempMapper.insert(modTemp);
                    return item;
                }
            } finally {
                // 释放锁
                redisLockUtil.unlock(lockKey, lockValue);
            }
        }
        return null;
    }

    @Override
    public void removeTemp(String modid) {
        XModTemp model = modTempMapper.selectOne(new QueryWrapper<XModTemp>().eq("modid", modid));
        Assert.notNull(model, "mod不存在");
        modTempMapper.deleteById(model.getId());
    }
}
