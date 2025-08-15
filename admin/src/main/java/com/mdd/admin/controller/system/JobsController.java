package com.mdd.admin.controller.system;

import com.alibaba.fastjson2.JSONObject;
import com.mdd.admin.service.system.IJobsService;
import com.mdd.common.aop.NotPower;
import com.mdd.admin.validate.commons.IdValidate;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.system.JobsCreateValidate;
import com.mdd.admin.validate.system.JobsSearchValidate;
import com.mdd.admin.validate.system.JobsUpdateValidate;
import com.mdd.admin.vo.system.JobsVo;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import com.mdd.common.enums.ErrorEnum;
import com.mdd.common.util.StringUtils;
import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/adminapi/dept.jobs")
@Api(tags = "系统岗位管理")
public class JobsController {

    @Resource
    IJobsService iJobsService;

    @NotPower
    @GetMapping("/all")
    @ApiOperation(value="所有岗位")
    public AjaxResult<List<JobsVo>> all() {
        List<JobsVo> list = iJobsService.all();
        return AjaxResult.success(list);
    }

    @GetMapping("/lists")
    @ApiOperation(value="岗位列表")
    public AjaxResult<Object> list(@Validated PageValidate pageValidate,
                                               @Validated JobsSearchValidate searchValidate) {
        if (StringUtils.isNotNull(searchValidate.getExport()) && searchValidate.getExport().equals(1)) {
            JSONObject ret = iJobsService.getExportData(pageValidate, searchValidate);
            return AjaxResult.success(ret);
        } else if (StringUtils.isNotNull(searchValidate.getExport()) && searchValidate.getExport().equals(2)) {
            String path = iJobsService.export(searchValidate);
            return AjaxResult.success(2, new JSONObject() {{
                put("url", path);
            }}, ErrorEnum.SHOW_MSG.getCode());
        } else {
            PageResult<JobsVo> list = iJobsService.list(pageValidate, searchValidate);
            return AjaxResult.success(list);
        }
    }

    @GetMapping("/detail")
    @ApiOperation(value="岗位详情")
    public AjaxResult<JobsVo> detail(@Validated @IDMust() @RequestParam("id") Integer id) {
        JobsVo vo = iJobsService.detail(id);
        return AjaxResult.success(vo);
    }

    @PostMapping("/add")
    @ApiOperation(value="岗位新增")
    public AjaxResult<Object> add(@Validated @RequestBody JobsCreateValidate createValidate) {
        iJobsService.add(createValidate);
        return AjaxResult.success();
    }

    @PostMapping("/edit")
    @ApiOperation(value="岗位编辑")
    public AjaxResult<Object> edit(@Validated @RequestBody JobsUpdateValidate updateValidate) {
        iJobsService.edit(updateValidate);
        return AjaxResult.success();
    }

    @PostMapping("/delete")
    @ApiOperation(value="岗位删除")
    public AjaxResult<Object> del(@Validated @RequestBody IdValidate idValidate) {
        iJobsService.del(idValidate.getId());
        return AjaxResult.success();
    }

}
