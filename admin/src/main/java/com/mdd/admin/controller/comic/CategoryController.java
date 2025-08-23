package com.mdd.admin.controller.comic;

import com.mdd.admin.aop.Log;
import com.mdd.admin.service.comic.ICategoryService;
import com.mdd.common.entity.comic.Category;
import com.mdd.admin.validate.comic.CategoryValidate;
import com.mdd.admin.validate.commons.IdValidate;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.vo.comic.CategoryDetailVo;
import com.mdd.admin.vo.comic.CategoryListVo;
import com.mdd.common.aop.NotLogin;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import javax.annotation.Resource;

@RestController
@RequestMapping("adminapi/category")
@Api(tags = "模块管理")
public class CategoryController {

    @Resource
    @Autowired
    ICategoryService iModuleService;
    
    @NotLogin
    @GetMapping("/all")
    @ApiOperation(value="模块列表")
    public AjaxResult<List<Category>> all() {
        List<Category> listModule = iModuleService.all();
        return AjaxResult.success(listModule);
    }
    
    @NotLogin
    @GetMapping("/list")
    @ApiOperation(value="资源列表")
    public AjaxResult<PageResult<CategoryListVo>> list(@Validated PageValidate pageValidate) {
        PageResult<CategoryListVo> list = iModuleService.list(pageValidate);
        return AjaxResult.success(list);
    }

    @NotLogin
    @GetMapping("/detail")
    @ApiOperation(value="详情")
    public AjaxResult<CategoryDetailVo> detail(@Validated @IDMust() @RequestParam("moduleid") String moduleid) {
        CategoryDetailVo detail = iModuleService.detail(moduleid);
        return AjaxResult.success(detail);
    }
    
    @NotLogin
    @PostMapping("/save")
    @ApiOperation(value="保存")
    public AjaxResult<Object> save(@Validated @RequestBody CategoryValidate saveValidate) {
        iModuleService.save(saveValidate);
        return AjaxResult.success();
    }
    
    @NotLogin
    @Log(title = "删除")
    @PostMapping("/del")
    @ApiOperation(value="删除")
    public AjaxResult<Object> del(@Validated @RequestBody IdValidate idValidate) {
        iModuleService.del(idValidate.getId());
        return AjaxResult.success();
    }
}
