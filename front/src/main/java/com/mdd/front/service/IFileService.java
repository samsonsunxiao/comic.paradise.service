package com.mdd.front.service;

import java.util.Map;

/**
 * 文件服务接口类
 */
public interface IFileService {
    /**
     * 文件新增
     *
     * @author fzr
     * @param params 文件信息参数
     */
    Integer fileAdd(Map<String, String> params);
}
