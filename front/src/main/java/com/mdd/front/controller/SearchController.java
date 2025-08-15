package com.mdd.front.controller;

import com.alibaba.fastjson.JSONObject;
import com.mdd.common.aop.NotLogin;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.validator.annotation.IDMust;
import com.mdd.front.service.ISearchService;
import com.mdd.front.service.IXModSearch;
import com.mdd.front.vo.xmod.XModSuggestVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/search")
@Api(tags = "搜索")
public class SearchController {


    @Resource
    ISearchService iSearchService;

    @NotLogin
    @GetMapping("/hotLists")
    @ApiOperation(value="搜索列表")
    public AjaxResult<JSONObject> hotLists() {
        JSONObject result = iSearchService.hotLists();
        return AjaxResult.success(result);
    }

    @Resource
    IXModSearch iModSearchService;
    
    @NotLogin
    @PostMapping("/suggest")
    @ApiOperation(value="游戏查询")
    public AjaxResult<List<XModSuggestVo>> list(@Validated @IDMust() @RequestParam("keyword") String keyword) {
        List<XModSuggestVo> listResond = iModSearchService.suggest(keyword);
        return AjaxResult.success(listResond);
    }
}
