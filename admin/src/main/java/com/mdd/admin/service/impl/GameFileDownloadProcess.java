package com.mdd.admin.service.impl;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.annotation.Scope;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.mdd.common.entity.mod.XModFiles;
import com.mdd.admin.service.IGameFileService;
import com.mdd.common.mapper.xmod.XModFilesMapper;
import com.mdd.common.util.ConfigUtils;
import com.mdd.common.util.MapUtils;
import com.mdd.common.util.YmlUtils;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

@Service
@Scope("prototype")
public class GameFileDownloadProcess implements IGameFileService {
    private String keyuri;
    @Autowired
    XModFilesMapper filesMapper;
    public GameFileDownloadProcess(String keyuri) {
        this.keyuri = keyuri;
    }
    
    public void setKeyUri(String keyuri){
        this.keyuri = keyuri;
    }

    @Override
    public void onDownloadStart(){
        XModFiles gameFile = filesMapper.selectOne(new QueryWrapper<XModFiles>().eq("keyuri", keyuri));
        if (gameFile != null){
            gameFile.setStatus("upload");
            filesMapper.updateById(gameFile);
        }
    }
    
    @Override
    @Transactional
    public void onDownloadComplete(String fileURL, String saveFile) {
        // Implement any logic to handle the post-download operations
        System.out.println("Download complete to upload oss keyuir: " + keyuri);
        //上传到OSS
        //Map<String, String> websiteConfig = ConfigUtils.get("website");
        //String strAliBucket = websiteConfig.getOrDefault("aliBucket", "");
        // CallPythonScript(saveFile);
        // Map<String, String> storageConfig = ConfigUtils.get("storage");
        // Map<String, String> aliyunConfig = MapUtils.jsonToMap(storageConfig.getOrDefault("aliyun", ""));
        // String strAliBucket = aliyunConfig.getOrDefault("bucket", "");
        // String accessKeyId = YmlUtils.get("ali-oss.accessKeyId");
        // String accessKeySecret = YmlUtils.get("ali-oss.accessKeySecret");
        // String endpoint = YmlUtils.get("ali-oss.endpoint");
        // // 创建OSSClient实例
        // OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        // try {
        //     // 创建PutObjectRequest对象。
        //     PutObjectRequest putObjectRequest = new PutObjectRequest(strAliBucket, keyuri, new File(saveFile));
        //     PutObjectResult result = ossClient.putObject(putObjectRequest);  
        //     System.out.println("Upload OSS Complete keyuri:" + keyuri + " etag:" + result.getETag());  
        //     File file = new File(saveFile);
        //     long fileSize = file.length(); // 获取文件大小（字节）
        //     if (filesMapper != null){
        //         XModFiles gameFile = filesMapper.selectOne(new QueryWrapper<XModFiles>().eq("keyuri", keyuri));
        //         if (gameFile != null){
        //             gameFile.setSize(fileSize);
        //             gameFile.setStatus("online");
        //             filesMapper.updateById(gameFile);
        //         }
        //     }else{
        //         System.out.println("onDownloadComplete imagesMapper is null");
        //     }
        //     file.delete();
        // } catch (OSSException oe) {
        //     System.out.println("keyuri:" + keyuri);
        //     System.out.println("Caught an OSSException, which means your request made it to OSS, "
        //             + "but was rejected with an error response for some reason.");
        //     System.out.println("Error Message:" + oe.getErrorMessage());
        //     System.out.println("Error Code:" + oe.getErrorCode());
        //     System.out.println("Request ID:" + oe.getRequestId());
        //     System.out.println("Host ID:" + oe.getHostId());
        // }finally {
        //     if (ossClient != null) {
        //         ossClient.shutdown();
        //     }
        // }
    }
}
