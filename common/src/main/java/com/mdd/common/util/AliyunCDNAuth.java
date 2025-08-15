package com.mdd.common.util;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class AliyunCDNAuth {

    // 生成 MD5 签名
    private static String generateMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }

    // 生成阿里云 URL 鉴权（A 方式）
    public static String generateAuthUrl(String domain, String filePath, String privateKey, int expireSeconds) {
        // 计算过期时间戳
        long timestamp = (new Date().getTime() / 1000) + expireSeconds;
        int rand = 0; // 随机数，通常为 0
        int uid = 0;  // 用户 ID，通常为 0

        // 生成签名字符串
        String signString = filePath + "-" + timestamp + "-" + rand + "-" + uid + "-" + privateKey;
        String md5hash = generateMD5(signString);

        // 拼接鉴权 URL
        String authUrl = domain + filePath + "?auth_key=" + timestamp + "-" + rand + "-" + uid + "-" + md5hash;
        return authUrl;
    }

    public static void main(String[] args) {
        String domain = "http://cdn2.x-mod.cn"; // 替换为你的 OSS 域名
        String filePath = "/resources/template/M4602322/0/1739428115573/Cyberpunk2077mod0912st24.zip"; // 替换为你的文件路径
        String privateKey = "app.x-mod.cn"; // 替换为你的阿里云私钥
        int expireSeconds = 3600; // 链接有效期（秒）

        String authUrl = generateAuthUrl(domain, filePath, privateKey, expireSeconds);
        System.out.println("Generated Auth URL: " + authUrl);
    }
}