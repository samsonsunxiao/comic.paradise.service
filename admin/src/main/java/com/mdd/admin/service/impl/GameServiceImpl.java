package com.mdd.admin.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.admin.service.IGameService;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.game.GameSaveValidate;
import com.mdd.admin.validate.game.GameSearchValidate;
import com.mdd.common.core.PageResult;
import com.mdd.common.exception.OperateException;
import com.mdd.common.mapper.xmod.GameMapper;
import com.mdd.common.mapper.xmod.ModuleGameMapper;
import com.mdd.common.mapper.xmod.XModFilesMapper;
import com.mdd.common.mapper.xmod.XModImagesMapper;
import com.mdd.common.mapper.xmod.XModMapper;
import com.mdd.common.util.YmlUtils;
import com.mdd.admin.vo.game.GameDetailVo;
import com.mdd.admin.vo.game.GameListVo;
import com.mdd.admin.vo.game.GameNameVo;
import com.mdd.common.entity.mod.XMod;
import com.mdd.common.entity.mod.XModFiles;
import com.mdd.common.entity.game.GameInfo;
import com.mdd.common.entity.game.XModuleGame;
import com.mdd.common.entity.mod.XModImages;
import com.mdd.common.entity.module.XModule;
import com.mdd.common.entity.module.XModuleGameVo;
import com.mdd.common.util.ConfigUtils;
import com.mdd.common.util.MultiFileDownloader;
import com.mdd.common.util.TimeUtils;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import javax.annotation.Resource;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.io.File;
import java.lang.InterruptedException;
/**
 * Game Org服务实现类
 */
@Slf4j
@Service
public class GameServiceImpl implements IGameService {
    
    @Resource
    @Autowired
    GameMapper gameMapper;
    
    
    @Resource
    @Autowired
    XModImagesMapper imagesMapper;
    
    @Resource
    @Autowired
    XModFilesMapper filesMapper;
    
    @Autowired
    ModuleGameMapper moduleGameMapper;
    
    @Resource
    @Autowired
    XModMapper modMapper;

    /**
     * 所有
     *
     * @author fzr
     * @return List<GameNameVo>
     */
    @Override
    public List<GameNameVo> all(String status) {
        List<GameInfo> listGame;
        if (!status.isEmpty()){
            listGame = gameMapper.selectList(new QueryWrapper<GameInfo>()
            .eq("status",status));
        }else{
            listGame = gameMapper.selectList(new QueryWrapper<GameInfo>().ne("status", "delete"));
        }
        
        List<GameNameVo> listResult = new ArrayList<>();
        for (GameInfo game : listGame) {
            GameNameVo vo = new GameNameVo();
            BeanUtils.copyProperties(game, vo);  
            listResult.add(vo);
        }  
        return listResult;
    }
    /**
     * 游戏列表
     *
     * @param pageValidate   分页参数
     * @param searchValidate 搜索参数
     * @return PageResult<XmodListedVo>
     * @author fzr
     */
    @Override
    public PageResult<GameListVo> list(PageValidate pageValidate, GameSearchValidate searchValidate) {
        Integer pageNo = pageValidate.getPage_no();
        Integer pageSize = pageValidate.getPage_size();
        MPJQueryWrapper<GameInfo> mpjQueryWrapper = new MPJQueryWrapper<GameInfo>()
                .selectAll(GameInfo.class)
                .orderByDesc("t.score");
        String text = searchValidate.getText();
        if (!text.isEmpty()){
            mpjQueryWrapper.nested(wq->wq.like("t.name", text).or()
            .like("t.descript", text));
        }
        if (!searchValidate.getName().isEmpty()){
            mpjQueryWrapper.nested(wq->wq.like("t.name", searchValidate.getName()));      
        }
        if (!searchValidate.getStatus().isEmpty())         {
            mpjQueryWrapper.nested(wq->wq.eq("t.status", searchValidate.getStatus()));  
        }
        if (searchValidate.getScoreMin() >= 0 && searchValidate.getScoreMax() > 0){
            mpjQueryWrapper.nested(wq->wq.between("t.score", searchValidate.getScoreMin(),searchValidate.getScoreMax()));
        }
        IPage<GameInfo> iPage = gameMapper.selectJoinPage(
                new Page<>(pageNo, pageSize),
                GameInfo.class,
                mpjQueryWrapper);
        List<GameListVo> list = new LinkedList<>();
        for (GameInfo item : iPage.getRecords()) {
            GameListVo vo = new GameListVo();
            BeanUtils.copyProperties(item, vo);
            String gid = vo.getGid();
            Long modCount = modMapper.selectCount(new
                QueryWrapper<XMod>().eq("gid", gid));
            if (modCount != null){
                vo.setModcount(modCount);
            }
            Long modOnlineCount = modMapper.selectCount(new
                QueryWrapper<XMod>().eq("gid", gid).eq("status","online"));
            if (modOnlineCount != null){
                vo.setModOnlineCount(modOnlineCount);
            }
            if (item.getCoverImage().isEmpty()){
                XModImages gameImage = imagesMapper.selectOne(new
                QueryWrapper<XModImages>().eq("uid", gid).ne("status","delete").last("limit 1"));
                if (gameImage != null){
                    vo.setCoverImage(gameImage.getUrl());
                }
            }
            
            list.add(vo);
        }
        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), list);
    }

    /**
     * 资源详情
     *
     * @param id 主键ID
     * @author fzr
     */
    @Override
    public GameDetailVo detail(String gid) {
        GameInfo model = gameMapper.selectOne(
                new QueryWrapper<GameInfo>()
                        .eq("gid", gid)
        );
        Assert.notNull(model, "游戏不存在");
        List<XModImages> listImages = imagesMapper.selectList(new
            QueryWrapper<XModImages>().eq("uid", model.getGid()).ne("status","delete"));
        List<XModFiles> listFiles = filesMapper.selectList(new
            QueryWrapper<XModFiles>().eq("uid", model.getGid()).ne("status","delete"));
        MPJQueryWrapper<XModuleGame> mpjQueryWrapper = new MPJQueryWrapper<XModuleGame>()
            .selectAll(XModuleGame.class)
            .select("module.name as modulename,module.id as mid")
            .innerJoin("xmod_modules module ON module.moduleid=t.moduleid")
            .eq("t.gid",gid);
        IPage<XModuleGameVo> iPage = moduleGameMapper.selectJoinPage(
                new Page<>(0, -1),
                XModuleGameVo.class,
                mpjQueryWrapper);
        List<XModule> listModules = new LinkedList<>();
        for (XModuleGameVo item : iPage.getRecords()){
            XModule module = new XModule();
            module.setId(item.getMid());
            module.setModuleid(item.getModuleid());
            module.setName(item.getModulename());
            listModules.add(module);
        }
        GameDetailVo vo = new GameDetailVo();
        BeanUtils.copyProperties(model, vo);
        vo.setImages(listImages);
        vo.setManagers(listFiles);
        vo.setModules(listModules);
        if (model.getOnlineTime() == 0){
            vo.setOnlineTime("");
        }else{
            vo.setOnlineTime(TimeUtils.timestampToDate(model.getOnlineTime()));
        }
        return vo;
    }

    @Autowired
    private ApplicationContext context;
    @Override
    @Transactional
    public void save(GameSaveValidate saveValidate)
    {
        GameInfo model = gameMapper.selectOne(
                new QueryWrapper<GameInfo>()
                        .eq("gid", saveValidate.getGid())
                        .ne("status", "delete"));
        Boolean isNew = false;
        if (model == null){
            model = new GameInfo();
            isNew = true;
        }
        model.setGid(saveValidate.getGid());
        model.setName(saveValidate.getName());
        model.setDescript(saveValidate.getDescript());
        model.setScore(saveValidate.getScore());
        model.setCoverImage(saveValidate.getCoverImage());
        model.setStatus(saveValidate.getStatus());
        model.setBanner(saveValidate.getBanner());
        if (saveValidate.getOnlineTime() == null || saveValidate.getOnlineTime().isEmpty()){
            model.setOnlineTime(System.currentTimeMillis() / 1000);
        }else{
            model.setOnlineTime(TimeUtils.dateToTimestamp(saveValidate.getOnlineTime()));
        }
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        if (isNew){
            gameMapper.insert(model);
        }
        else{
            gameMapper.updateById(model);
        }
        // 处理module
        List<XModuleGame> listModuleGame = moduleGameMapper.selectList(new
            QueryWrapper<XModuleGame>().eq("gid", saveValidate.getGid()));
        List<String> moduleList = listModuleGame.stream().map(XModuleGame::getModuleid).collect(Collectors.toList());
        List<String> listAdd = saveValidate.getModules().stream()
            .filter(moduleid1 -> moduleList.stream().noneMatch(moduleid2 -> moduleid1.equals(moduleid2))).collect(Collectors.toList());
        for (String moduleid : listAdd) {
            XModuleGame moduleGame = new XModuleGame();
            moduleGame.setModuleid(moduleid);
            moduleGame.setGid(saveValidate.getGid());
            moduleGameMapper.insert(moduleGame);
        }
        List<String> listDel = moduleList.stream()
            .filter(moduleid1 -> saveValidate.getModules().stream().noneMatch(moduleid2 -> moduleid1.equals(moduleid2))).collect(Collectors.toList());
        for (String moduleid : listDel) {
            moduleGameMapper.delete(new QueryWrapper<XModuleGame>().eq("moduleid", moduleid).eq("gid", saveValidate.getGid()));
        }
        // 处理图片
        processImages(saveValidate.getImages(),saveValidate.getGid());
        // 处理前置修改器包
        processFiles(saveValidate.getManagers(),saveValidate.getGid());
    }

    public void processImages(List<XModImages> listImagesR, String gid){
        List<XModImages> listImages = imagesMapper.selectList(new
            QueryWrapper<XModImages>().eq("uid", gid));
            
        List<XModImages> listDel = listImages.stream()
            .filter(image1 -> listImagesR.stream().noneMatch(image2 -> image1.getKeyuri().equals(image2.getKeyuri())))
            .collect(Collectors.toList());
        for (XModImages item : listImagesR) {
            item.setType("game");
            if (item.getStatus().equals("local")){
                item.setStatus("offline");
            }
            Optional<XModImages> result = listImages.stream()
                .filter(image -> image.getKeyuri().equals(item.getKeyuri())) // 假设 XModImages 类中有 getId() 方法
                .findFirst();
            XModImages findItem = result.orElse(null);            
            if (findItem == null){
                imagesMapper.insert(item);
            }
            else{
                item.setId(findItem.getId());
                imagesMapper.updateById(item);
            }
        }
        for (XModImages item : listDel) {
            item.setStatus("delete");
            imagesMapper.updateById(item);
        }
        final List<XModImages> listImagesDl = listImagesR;
        String savePath = YmlUtils.get("app.upload-directory");
        File saveDir = new File(savePath);
        if(!saveDir.exists()) {
            if (!saveDir.mkdirs()) {
                throw new OperateException("创建下载目录失败");
            }
        }
        final String strSaveDir = savePath;
        //异步把图片都上传到阿里云OSS中
        Thread thread = new Thread(new Runnable() 
        {
            @Override
            public void run() {
                try {
                     // 线程池，控制并发的线程数量
                    ConcurrentHashMap<String, Boolean> uploadStatus = new ConcurrentHashMap<>();
                    ExecutorService executor = Executors.newFixedThreadPool(5);  // 5 个线程并发
                    for (XModImages item : listImagesDl) {
                        if (uploadStatus.getOrDefault(item.getKeyuri(), false)) {
                            continue; // 另一个线程已经在处理该文件
                        }
                        uploadStatus.put(item.getKeyuri(), true);
                        String url = item.getUrl();
                        // 根据文件 URL 动态生成保存路径
                        String fileName = url.substring(url.lastIndexOf('/') + 1);
                        String saveFile = strSaveDir + fileName;
                        //检测OSS是否存在。存在就不上传了
                        ObjectMetadata metadata = getFileMeta(item.getKeyuri());
                        if (metadata != null){
                            XModImages gameImage = imagesMapper.selectOne(new QueryWrapper<XModImages>().eq("keyuri", item.getKeyuri()));
                            if (gameImage != null){
                                gameImage.setSize(metadata.getContentLength());
                                gameImage.setStatus("online");
                                imagesMapper.updateById(gameImage);
                            }
                            File file = new File(saveFile);
                            file.delete();
                            continue;
                        }
                        GameImageDownloadProcess listener = context.getBean(GameImageDownloadProcess.class, item.getKeyuri());
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

    public void processFiles(List<XModFiles> listFilesR, String gid){
        List<XModFiles> listFiles = filesMapper.selectList(new
            QueryWrapper<XModFiles>().eq("uid", gid));
            
        List<XModFiles> listDel = listFiles.stream()
            .filter(file1 -> listFilesR.stream().noneMatch(file2 -> file1.getKeyuri().equals(file2.getKeyuri())))
            .collect(Collectors.toList());
        for (XModFiles item : listFilesR) {
            item.setType("master");
            item.setStatus("online");
            item.setUid(gid);
            Optional<XModFiles> result = listFiles.stream()
                .filter(file -> file.getKeyuri().equals(item.getKeyuri())) // 假设 XModImages 类中有 getId() 方法
                .findFirst();
            XModFiles findItem = result.orElse(null);            
            if (findItem == null){
                filesMapper.insert(item);
            }
            else{
                item.setId(findItem.getId());
                filesMapper.updateById(item);
            }
        }
        for (XModFiles item : listDel) {
            item.setStatus("delete");
            filesMapper.updateById(item);
        }
    }
    public boolean checkKeyUriExisted(String keyuri){
        return getFileMeta(keyuri) != null;
    }
    
    public static ObjectMetadata getFileMeta(String objectName) {
        Map<String, String> websiteConfig = ConfigUtils.get("website");
        String strAliBucket = websiteConfig.getOrDefault("aliBucket", "");
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
}
