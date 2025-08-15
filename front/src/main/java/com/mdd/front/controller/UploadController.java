package com.mdd.front.controller;

import com.mdd.common.aop.NotLogin;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.enums.AlbumEnum;
import com.mdd.common.enums.FileEnum;
import com.mdd.common.exception.OperateException;
import com.mdd.common.plugin.storage.StorageDriver;
import com.mdd.common.plugin.storage.UploadFilesVo;
import com.mdd.common.util.StringUtils;
import com.mdd.front.FrontThreadLocal;
import com.mdd.front.service.IFileService;
import com.mdd.front.vo.upload.UploadImagesVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@Api(tags = "上传管理")
public class UploadController {

    @Resource
    IFileService iFileService;

    @NotLogin
    @PostMapping("/image")
    @ApiOperation(value="上传图片")
    public AjaxResult<UploadImagesVo> image(HttpServletRequest request) {
        MultipartFile multipartFile;
        try {
            multipartFile = ((MultipartRequest) request).getFile("file");
        } catch (Exception e) {
            throw new OperateException("请正确选择上传图片!");
        }

        if (multipartFile == null) {
            throw new OperateException("请选择上传图片!");
        }

        String folder = "image";
        if (StringUtils.isNotEmpty(request.getParameter("dir"))) {
            folder += "/" + request.getParameter("dir");
        }

        StorageDriver storageDriver = new StorageDriver();
        UploadFilesVo vo = storageDriver.upload(multipartFile, folder, AlbumEnum.IMAGE.getCode());
        System.out.println(vo);

        Map<String, String> params = new HashMap<>();
        params.put("cid", "0");
        params.put("uid", String.valueOf(FrontThreadLocal.getUserId()));
        params.put("source", String.valueOf(FileEnum.SOURCE_USER.getCode()));
        params.put("type", String.valueOf(FileEnum.IMAGE_TYPE.getCode()));
        params.put("name", vo.getName());
        params.put("url", vo.getUrl());
        Integer id = iFileService.fileAdd(params);

        UploadImagesVo upVo = new UploadImagesVo();
        upVo.setUrl(vo.getUrl());
        upVo.setUri(vo.getPath());
        upVo.setId(id);
        upVo.setType(FileEnum.IMAGE_TYPE.getCode());
        upVo.setName(vo.getName());
        upVo.setCid(0);
        return AjaxResult.success(upVo);
    }

}
