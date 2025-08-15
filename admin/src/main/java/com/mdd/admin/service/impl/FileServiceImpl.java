package com.mdd.admin.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mdd.admin.service.IFileService;
import com.mdd.admin.validate.file.FileCateValidate;
import com.mdd.admin.validate.file.FileSearchValidate;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.vo.album.FileCateVo;
import com.mdd.admin.vo.album.FileVo;
import com.mdd.common.config.GlobalConfig;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.file.File;
import com.mdd.common.entity.file.FileCate;
import com.mdd.common.mapper.album.FileCateMapper;
import com.mdd.common.mapper.album.FileMapper;
import com.mdd.common.plugin.storage.StorageDriver;
import com.mdd.common.util.*;
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
     * 相册文件列表
     *
     * @author fzr
     * @param pageValidate 分页参数
     * @param searchValidate 搜索参数
     * @return PageResult<AlbumVo>
     */
    @Override
    public PageResult<FileVo> fileList(PageValidate pageValidate, FileSearchValidate searchValidate) {
        Integer page  = pageValidate.getPage_no();
        Integer limit = pageValidate.getPage_size();

        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(File.class, info->
                    !info.getColumn().equals("aid") &&
                    !info.getColumn().equals("uid") &&
                    !info.getColumn().equals("delete_time"))
                .isNull("delete_time")
                .orderByDesc("id");

        if (StringUtils.isNotNull(searchValidate.getCid())) {
            queryWrapper.eq("cid", searchValidate.getCid());
        }

        fileMapper.setSearch(queryWrapper, searchValidate, new String[]{
                "=:type:int",
                "like:name@name:str",
                "=:source:int"
        });

        IPage<File> iPage = fileMapper.selectPage(new Page<>(page, limit), queryWrapper);

        String engine = ConfigUtils.get("storage", "default", "local");
        engine = engine.equals("") ? "local" : engine;

        List<FileVo> list = new ArrayList<>();
        for (File file : iPage.getRecords()) {
            FileVo vo = new FileVo();
            BeanUtils.copyProperties(file, vo);
            vo.setUrl(UrlUtils.toAdminAbsoluteUrl(file.getUri()));

            if (engine.equals("local")) {
                vo.setUri(GlobalConfig.adminPublicPrefix + "/" + file.getUri());
            } else {
                vo.setUri(file.getUri());
            }

            vo.setCreateTime(TimeUtils.timestampToDate(file.getCreateTime()));
            vo.setUpdateTime(TimeUtils.timestampToDate(file.getUpdateTime()));
            list.add(vo);
        }

        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), list);
    }

    /**
     * 文件重命名
     *
     * @author fzr
     * @param id 文件ID
     * @param name 文件名称
     */
    @Override
    public void fileRename(Integer id, String name) {
        File file = fileMapper.selectOne(new QueryWrapper<File>()
                .select("id", "name")
                .eq("id", id)
                .isNull("delete_time"));

        Assert.notNull(file, "文件丢失！");

        file.setName(name);
        file.setUpdateTime(System.currentTimeMillis() / 1000);
        fileMapper.updateById(file);
    }

    /**
     * 文件移动
     *
     * @author fzr
     * @param ids 文件ID
     * @param cid 类目ID
     */
    @Override
    public void fileMove(List<Integer> ids, Integer cid) {
        List<File> files = fileMapper.selectList(new QueryWrapper<File>()
                .select("id", "name")
                .in("id", ids)
                .isNull("delete_time"));

        Assert.notNull(files, "文件丢失！");

        if (cid > 0) {
            Assert.notNull(fileCateMapper.selectOne(
                    new QueryWrapper<FileCate>()
                            .eq("id", cid)
                            .isNull("delete_time")
            ), "类目已不存在！");
        }

        for (File file : files) {
            file.setCid(cid);
            file.setUpdateTime(System.currentTimeMillis() / 1000);
            fileMapper.updateById(file);
        }
    }

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
        album.setName(name);
        album.setUri(params.get("url"));
        album.setCreateTime(System.currentTimeMillis() / 1000);
        album.setUpdateTime(System.currentTimeMillis() / 1000);
        fileMapper.insert(album);
        return album.getId();
    }

    /**
     * 相册文件删除
     *
     * @author fzr
     * @param ids 文件ID
     */
    @Override
    public void fileDel(List<Integer> ids) {
        List<File> files = fileMapper.selectList(new QueryWrapper<File>()
                .in("id", ids)
                .isNull("delete_time"));

        Assert.notNull(files, "文件丢失！");

        for (File file : files) {
            StorageDriver driver = new StorageDriver();
            driver.deleteFile(file.getUri());
            file.setDeleteTime(System.currentTimeMillis() / 1000);
            fileMapper.updateById(file);
        }
    }

    /**
     * 相册分类列表
     *
     * @param searchValidate 搜索参数
     * @return JSONArray
     */
    @Override
    public JSONObject cateList(FileSearchValidate searchValidate) {
        QueryWrapper<FileCate> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(FileCate.class, info->
                        !info.getColumn().equals("delete_time"))
                .isNull("delete_time")
                .orderByDesc("id");

        if (StringUtils.isNotNull(searchValidate.getType()) && searchValidate.getType() > 0) {
            queryWrapper.eq("type", searchValidate.getType());
        }

        if (StringUtils.isNotNull(searchValidate.getName()) && StringUtils.isNotEmpty(searchValidate.getName())) {
            queryWrapper.like("name", searchValidate.getName());
        }

        List<FileCate> fileCateList = fileCateMapper.selectList(queryWrapper);

        List<FileCateVo> lists = new LinkedList<>();
        for (FileCate fileCate : fileCateList) {
            FileCateVo vo = new FileCateVo();
            BeanUtils.copyProperties(fileCate, vo);

            vo.setCreateTime(TimeUtils.timestampToDate(fileCate.getCreateTime()));
            vo.setUpdateTime(TimeUtils.timestampToDate(fileCate.getUpdateTime()));
            lists.add(vo);
        }

        JSONArray jsonArray = JSONArray.parseArray(JSONArray.toJSONString(lists));

        JSONObject result = new JSONObject();

        result.put("lists", ListUtils.listToTree(jsonArray, "id", "pid", "children"));

        return result;
    }

    /**
     * 分类新增
     *
     * @author fzr
     * @param cateValidate 分类参数
     */
    @Override
    public void cateAdd(FileCateValidate cateValidate) {
        FileCate albumCate = new FileCate();
        albumCate.setType(cateValidate.getType());
        albumCate.setPid(cateValidate.getPid());
        albumCate.setName(cateValidate.getName());
        albumCate.setCreateTime(System.currentTimeMillis() / 1000);
        albumCate.setUpdateTime(System.currentTimeMillis() / 1000);
        fileCateMapper.insert(albumCate);
    }

    /**
     * 分类重命名
     *
     * @author fzr
     * @param id 分类ID
     * @param name 分类名称
     */
    @Override
    public void cateRename(Integer id, String name) {
        FileCate fileCate = fileCateMapper.selectOne(
                new QueryWrapper<FileCate>()
                        .select("id", "name")
                        .eq("id", id)
                        .isNull("delete_time"));

        Assert.notNull(fileCate, "分类已不存在！");

        fileCate.setName(name);
        fileCate.setUpdateTime(System.currentTimeMillis() / 1000);
        fileCateMapper.updateById(fileCate);
    }

    /**
     * 分类删除
     *
     * @author fzr
     * @param id 分类ID
     */
    @Override
    public void cateDel(Integer id) {
        FileCate albumCate = fileCateMapper.selectOne(
                new QueryWrapper<FileCate>()
                        .select("id", "name")
                        .eq("id", id)
                        .isNull("delete_time"));

        Assert.notNull(albumCate, "分类已不存在！");

        Assert.isNull(fileMapper.selectOne(new QueryWrapper<File>()
                .select("id", "cid", "name")
                .eq("cid", id)
                .isNull("delete_time")
                .last("limit 1")
            ), "当前分类正被使用中,不能删除！");

        albumCate.setDeleteTime(System.currentTimeMillis() / 1000);
        fileCateMapper.updateById(albumCate);
    }

}
