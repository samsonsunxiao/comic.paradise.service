package com.mdd.common.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MultiFileDownloader {

    public static class DownloadTask implements Runnable {
        private String fileURL;
        private String saveDir;
        private IDownloadProcessListener downloadProcessListener;
        
        public DownloadTask(String fileURL, String saveDir, IDownloadProcessListener listener) {
            this.fileURL = fileURL;
            this.saveDir = saveDir;
            this.downloadProcessListener = listener;
        }

        @Override
        public void run() {
            System.out.println("Downloading: " + fileURL);
            try {
                if (downloadProcessListener != null) {
                    downloadProcessListener.onDownloadStart();
                }
                downloadFileWithHttpClient(fileURL, saveDir);
                System.out.println("Downloaded: " + fileURL);
                if (downloadProcessListener != null) {
                    downloadProcessListener.onDownloadComplete(fileURL, saveDir);
                }
            } catch (IOException e) {
                System.err.println("Failed to download: " + fileURL + " - " + e.getMessage());
            }
        }

        // 使用 Apache HttpClient 下载文件
        private void downloadFileWithHttpClient(String fileURL, String saveDir) throws IOException {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet request = new HttpGet(fileURL);
            request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36");
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    try (InputStream inputStream = entity.getContent();
                         FileOutputStream outputStream = new FileOutputStream(saveDir)) {

                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, length);
                        }
                    }
                }
                EntityUtils.consume(entity);
            }
        }
    }

    public static void main(String[] args) {
        // 下载文件的 URL 列表
        String[] fileURLs = {
            "https://example.com/file1.zip",
            "https://example.com/file2.zip",
            "https://example.com/file3.zip"
            // 更多文件 URL
        };

        // 线程池，控制并发的线程数量
        ExecutorService executor = Executors.newFixedThreadPool(5);  // 5 个线程并发

        for (String fileURL : fileURLs) {
            // 根据文件 URL 动态生成保存路径
            String fileName = fileURL.substring(fileURL.lastIndexOf('/') + 1);
            String savePath = "downloads/" + fileName;

            // 提交下载任务
            executor.execute(new DownloadTask(fileURL, savePath, null));
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

        System.out.println("All downloads completed!");
    }
}