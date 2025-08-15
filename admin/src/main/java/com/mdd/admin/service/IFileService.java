package com.mdd.admin.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.mdd.admin.validate.file.FileCateValidate;
import com.mdd.admin.validate.file.FileSearchValidate;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.vo.album.FileVo;
import com.mdd.common.core.PageResult;

import java.util.List;
import java.util.Map;

/**
 * 文件服务接口类
 */
public interface IFileService {

    /**
     * 文件列表
     *
     * @author fzr
     * @param pageValidate 分页参数
     * @param searchValidate 其他搜索参数
     * @return PageResult<AlbumVo>
     */
    PageResult<FileVo> fileList(PageValidate pageValidate, FileSearchValidate searchValidate);

    /**
     * 文件重命名
     *
     * @param id 文件ID
     * @param name 文件名称
     */
    void fileRename(Integer id, String name);

    /**
     * 文件移动
     *
     * @author fzr
     * @param ids 文件ID
     * @param cid 类目ID
     */
    void fileMove(List<Integer> ids, Integer cid);

    /**
     * 文件新增
     *
     * @author fzr
     * @param params 文件信息参数
     */
    Integer fileAdd(Map<String, String> params);

    /**
     * 文件删除
     *
     * @author fzr
     * @param ids 文件ID
     */
    void fileDel(List<Integer> ids);

    /**
     * 分类列表
     *
     * @author fzr
     * @param searchValidate 搜索参数
     * @return JSONArray
     */
    JSONObject cateList(FileSearchValidate searchValidate);

    /**
     * 分类新增
     *
     * @author fzr
     * @param cateValidate 分类参数
     */
    void cateAdd(FileCateValidate cateValidate);

    /**
     * 分类编辑
     *
     * @author fzr
     * @param id 分类ID
     * @param name 分类名称
     */
    void cateRename(Integer id, String name);

    /**
     * 分类删除
     *
     * @author fzr
     * @param id 分类ID
     */
    void cateDel(Integer id);

}
