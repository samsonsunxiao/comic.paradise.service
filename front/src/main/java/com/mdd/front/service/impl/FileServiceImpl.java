package com.mdd.front.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mdd.common.config.GlobalConfig;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.file.File;
import com.mdd.common.entity.file.FileCate;
import com.mdd.common.mapper.album.FileCateMapper;
import com.mdd.common.mapper.album.FileMapper;
import com.mdd.common.util.*;
import com.mdd.front.service.IFileService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 文件服务实现类
 */
@Service
public class FileServiceImpl implements IFileService {

    @Resource
    FileMapper fileMapper;

    @Resource
    FileCateMapper fileCateMapper;

    /**
     * 文件新增
     *
     * @author fzr
     * @param params 文件信息参数
     */
    @Override
    public Integer fileAdd(Map<String, String> params) {
        String name = params.get("name");
        if (name.length() >= 100) {
            name = name.substring(0, 99);
        }

        File album = new File();
        album.setCid(Integer.parseInt(params.get("cid") == null ? "0" : params.get("cid")));
        album.setSourceId(Integer.parseInt(params.get("uid") == null ? "0" : params.get("uid")));
        album.setType(Integer.parseInt(params.get("type")));
        album.setSource(Integer.parseInt(params.get("source") == null ? "0" : params.get("source")));
        album.setName(name);
        album.setUri(params.get("url"));
        album.setCreateTime(System.currentTimeMillis() / 1000);
        album.setUpdateTime(System.currentTimeMillis() / 1000);
        fileMapper.insert(album);
        return album.getId();
    }

}
