package com.mdd.admin.controller.system;

import com.alibaba.fastjson2.JSONArray;
import com.mdd.admin.service.system.IDeptService;
import com.mdd.common.aop.NotPower;
import com.mdd.admin.validate.commons.IdValidate;
import com.mdd.admin.validate.system.DeptCreateValidate;
import com.mdd.admin.validate.system.DeptSearchValidate;
import com.mdd.admin.validate.system.DeptUpdateValidate;
import com.mdd.admin.vo.system.DeptVo;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/adminapi/dept.dept")
@Api(tags = "系统部门管理")
public class DeptController {

    @Resource
    IDeptService deptService;

    @NotPower
    @GetMapping("/all")
    @ApiOperation(value="部门所有")
    public AjaxResult<JSONArray> all() {
        JSONArray list = deptService.all();
        return AjaxResult.success(list);
    }

    @GetMapping("/lists")
    @ApiOperation(value="部门列表")
    public AjaxResult<JSONArray> list(@Validated DeptSearchValidate searchValidate) {
        JSONArray list = deptService.list(searchValidate);
        return AjaxResult.success(list);
    }

    @GetMapping("/detail")
    @ApiOperation(value="部门详情")
    public AjaxResult<DeptVo> detail(@Validated @IDMust() @RequestParam("id") Integer id) {
        DeptVo vo = deptService.detail(id);
        return AjaxResult.success(vo);
    }

    @PostMapping("/add")
    @ApiOperation(value="部门新增")
    public AjaxResult<Object> add(@Validated @RequestBody DeptCreateValidate createValidate) {
        deptService.add(createValidate);
        return AjaxResult.success();
    }

    @PostMapping("/edit")
    @ApiOperation(value="部门编辑")
    public AjaxResult<Object> edit(@Validated @RequestBody DeptUpdateValidate updateValidate) {
        deptService.edit(updateValidate);
        return AjaxResult.success();
    }

    @PostMapping("/delete")
    @ApiOperation(value="部门删除")
    public AjaxResult<Object> del(@Validated @RequestBody IdValidate idValidate) {
        deptService.del(idValidate.getId());
        return AjaxResult.success();
    }

}
