package com.mdd.common.util;

public interface IDownloadProcessListener {
    void onDownloadStart();
    void onDownloadComplete(String fileURL, String saveDir);
}