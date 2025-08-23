package com.mdd.admin.controller.comic;

import com.mdd.admin.service.comic.IArticleService;
import com.mdd.admin.validate.comic.ArticleSearchValidate;
import com.mdd.admin.validate.comic.ArticleValidate;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.vo.comic.ArticleListVo;
import com.mdd.admin.vo.comic.SummaryVo;
import com.mdd.admin.vo.comic.ArticleDetailVo;
import com.mdd.common.aop.NotLogin;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import com.mdd.common.validator.annotation.IDMust;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

import java.util.List;
@RestController
@RequestMapping("adminapi/comic")
@Api(tags = "漫画 资源管理")
public class ArticleController {

    @Resource
    @Autowired
    IArticleService iArticleService;

    //@Resource
    //AliOSSService aliOSSService;
    @NotLogin
    @GetMapping("/all")
    @ApiOperation(value="资源列表")
    public AjaxResult<List<SummaryVo>> all(@Validated @IDMust() @RequestParam("status") String status) {
        List<SummaryVo> lsArticle = iArticleService.all(status);
        return AjaxResult.success(lsArticle);
    }
    @NotLogin
    @GetMapping("/list")
    @ApiOperation(value="资源列表")
    public AjaxResult<PageResult<ArticleListVo>> list(@Validated PageValidate pageValidate,
                                                     @Validated ArticleSearchValidate searchValidate) {
        PageResult<ArticleListVo> lsArticle = iArticleService.list(pageValidate, searchValidate);
        return AjaxResult.success(lsArticle);
    }
    @NotLogin
    @GetMapping("/detail")
    @ApiOperation(value="详情")
    public AjaxResult<ArticleDetailVo> detail(@Validated @IDMust() @RequestParam("gid") String gid) {
        ArticleDetailVo detailGame = iArticleService.detail(gid);
        return AjaxResult.success(detailGame);
    }
    @NotLogin
    @PostMapping("/save")
    @ApiOperation(value="保存")
    public AjaxResult<Object> save(@Validated @RequestBody ArticleValidate saveValidate) {
        iArticleService.save(saveValidate);
        return AjaxResult.success();
    }
}
