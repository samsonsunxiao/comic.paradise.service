package com.mdd.common.plugin.storage.engine;

import com.google.gson.Gson;
import com.mdd.common.exception.OperateException;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * 七牛云存储
 */
public class QiniuStorage {

    /**
     * 存储配置
     */
    private final Map<String, String> config;

    /**
     * 构造方法
     */
    public QiniuStorage(Map<String, String> config) {
        this.config = config;
    }

    /**
     * 鉴权令牌
     *
     * @author fzr
     * @return String
     */
    public String upToken() {
        String accessKey = this.config.getOrDefault("access_key", "");
        String secretKey = this.config.getOrDefault("secret_key", "");
        String bucket    = this.config.getOrDefault("bucket", "");
        Auth auth = Auth.create(accessKey, secretKey);
        return auth.uploadToken(bucket);
    }

    /**
     * 上传文件
     *
     * @author fzr
     * @param multipartFile 文件对象
     * @param key 保存文件的名称
     */
    public void upload(MultipartFile multipartFile, String key) {
        Configuration cfg = new Configuration(Region.region2());
        UploadManager uploadManager = new UploadManager(cfg);
        try {
            Response response = uploadManager.put(multipartFile.getBytes(), key, this.upToken());
            DefaultPutRet ret = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println(ret);
        } catch (IOException ex) {
            throw new OperateException(ex.getMessage());
        }
    }

    public void delete(String filePath) {
        String accessKey = this.config.getOrDefault("access_key", "");
        String secretKey = this.config.getOrDefault("secret_key", "");
        String bucket    = this.config.getOrDefault("bucket", "");
        Auth auth = Auth.create(accessKey, secretKey);
        Configuration cfg = new Configuration(Region.region2());
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            Response response = bucketManager.delete(bucket, filePath);
            System.out.println(response);
        } catch (QiniuException e) {
            throw new RuntimeException(e);
        }
    }
}
