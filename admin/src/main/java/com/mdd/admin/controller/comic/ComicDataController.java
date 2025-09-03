package com.mdd.admin.controller.comic;

import com.mdd.common.aop.NotLogin;
import com.mdd.common.core.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mdd.admin.service.comic.IComicDataService;

@RestController
@RequestMapping("adminapi/comic")
@Api(tags = "漫画数据管理")
public class ComicDataController {

    @Autowired
    private IComicDataService iComicDataService;

    @Value("${assistant.service.url:http://localhost:8085}")
    private String assistantServiceUrl;

    @GetMapping("/data")
    @NotLogin
    @ApiOperation(value="获取漫画数据")
    public AjaxResult<Object> getAllComics() {
        // 请求立即返回Redis键
        return AjaxResult.success(iComicDataService.queryComic());
    }
}