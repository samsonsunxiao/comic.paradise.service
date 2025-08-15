package com.mdd.admin.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.mdd.admin.aop.Log;
import com.mdd.admin.service.IFileService;
import com.mdd.admin.validate.file.FileCateValidate;
import com.mdd.admin.validate.file.FileMoveValidate;
import com.mdd.admin.validate.file.FileRenameValidate;
import com.mdd.admin.validate.file.FileSearchValidate;
import com.mdd.admin.validate.commons.IdValidate;
import com.mdd.admin.validate.commons.IdsValidate;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.vo.album.FileVo;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/adminapi/file")
@Api(tags = "文件数据管理")
public class FileController {

    @Resource
    IFileService fileService;

    @GetMapping("/lists")
    @ApiOperation(value="文件列表")
    public AjaxResult<PageResult<FileVo>> fileList(@Validated PageValidate pageValidate,
                                                   @Validated FileSearchValidate searchValidate) {
        PageResult<FileVo> voPageResult = fileService.fileList(pageValidate, searchValidate);
        return AjaxResult.success(voPageResult);
    }

    @Log(title = "文件重命名")
    @PostMapping("/rename")
    @ApiOperation(value="文件重命名")
    public AjaxResult<Object> rename(@Validated @RequestBody FileRenameValidate renameValidate) {
        fileService.fileRename(renameValidate.getId(), renameValidate.getName());
        return AjaxResult.success();
    }

    @Log(title = "文件移动")
    @PostMapping("/move")
    @ApiOperation(value="相册文件移动")
    public AjaxResult<Object> move(@Validated @RequestBody FileMoveValidate moveValidate) {
        fileService.fileMove(moveValidate.getIds(), moveValidate.getCid());
        return AjaxResult.success();
    }

    @Log(title = "文件删除")
    @PostMapping("/delete")
    @ApiOperation(value="相册文件删除")
    public AjaxResult<Object> del(@Validated @RequestBody IdsValidate idsValidate) {
        fileService.fileDel(idsValidate.getIds());
        return AjaxResult.success();
    }

    @GetMapping("/listCate")
    @ApiOperation(value="文件分类列表")
    public AjaxResult<JSONObject> cateList(@Validated FileSearchValidate searchValidate) {
        JSONObject result = fileService.cateList(searchValidate);
        return AjaxResult.success(result);
    }

    @Log(title = "分类新增")
    @PostMapping("/addCate")
    @ApiOperation(value="分类新增")
    public AjaxResult<Object> cateAdd(@Validated @RequestBody FileCateValidate cateValidate) {
        fileService.cateAdd(cateValidate);
        return AjaxResult.success();
    }

    @Log(title = "相册分类重命名")
    @PostMapping("/editCate")
    @ApiOperation(value="相册分类重命名")
    public AjaxResult<Object> cateRename(@Validated @RequestBody FileRenameValidate renameValidate) {
        fileService.cateRename(renameValidate.getId(), renameValidate.getName());
        return AjaxResult.success();
    }

    @Log(title = "相册分类删除")
    @PostMapping("/delCate")
    @ApiOperation(value="相册分类删除")
    public AjaxResult<Object> cateDel(@Validated @RequestBody IdValidate idValidate) {
        fileService.cateDel(idValidate.getId());
        return AjaxResult.success();
    }

}
