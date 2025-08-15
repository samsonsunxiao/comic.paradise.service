package com.mdd.admin.controller.system;

import com.alibaba.fastjson2.JSONObject;
import com.mdd.admin.AdminThreadLocal;
import com.mdd.admin.aop.Log;
import com.mdd.common.aop.NotPower;
import com.mdd.admin.service.admin.IAdminService;
import com.mdd.admin.validate.commons.IdValidate;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.system.SystemAdminCreateValidate;
import com.mdd.admin.validate.system.SystemAdminSearchValidate;
import com.mdd.admin.validate.system.SystemAdminUpInfoValidate;
import com.mdd.admin.validate.system.SystemAdminUpdateValidate;
import com.mdd.admin.vo.system.SystemAuthAdminDetailVo;
import com.mdd.admin.vo.system.SystemAuthAdminListedVo;
import com.mdd.admin.vo.system.SystemAuthAdminSelvesVo;
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

@RestController
@RequestMapping("/adminapi/auth.admin")
@Api(tags = "系统用户管理")
public class SystemAuthAdminController {

    @Resource
    IAdminService iSystemAuthAdminService;

    @GetMapping("/lists")
    @ApiOperation(value="管理员列表")
    public AjaxResult<Object> list(@Validated PageValidate pageValidate,
                                                                @Validated SystemAdminSearchValidate searchValidate) {
        if (StringUtils.isNotNull(searchValidate.getExport()) && searchValidate.getExport().equals(1)) {
            JSONObject ret = iSystemAuthAdminService.getExportData(pageValidate, searchValidate);
            return AjaxResult.success(ret);
        } else if (StringUtils.isNotNull(searchValidate.getExport()) && searchValidate.getExport().equals(2)) {
            String path = iSystemAuthAdminService.export(searchValidate);
            return AjaxResult.success(2, new JSONObject() {{
                put("url", path);
            }}, ErrorEnum.SHOW_MSG.getCode());
        } else {
            PageResult<SystemAuthAdminListedVo> list = iSystemAuthAdminService.list(pageValidate, searchValidate);
            return AjaxResult.success(list);
        }
    }

    @NotPower
    @GetMapping("/self")
    @ApiOperation(value="管理员信息")
    public AjaxResult<SystemAuthAdminSelvesVo> self() {
        Integer adminId = AdminThreadLocal.getAdminId();
        SystemAuthAdminSelvesVo vo = iSystemAuthAdminService.self(adminId);
        return AjaxResult.success(vo);
    }

    @GetMapping("/detail")
    @ApiOperation(value="管理员详情")
    public AjaxResult<SystemAuthAdminDetailVo> detail(@Validated @IDMust() @RequestParam("id") Integer id) {
        SystemAuthAdminDetailVo vo = iSystemAuthAdminService.detail(id);
        return AjaxResult.success(vo);
    }

    @Log(title = "管理员新增")
    @PostMapping("/add")
    @ApiOperation(value="管理员新增")
    public AjaxResult<Object> add(@Validated @RequestBody SystemAdminCreateValidate createValidate) {
        iSystemAuthAdminService.add(createValidate);
        return AjaxResult.success();
    }

    @Log(title = "管理员编辑")
    @PostMapping("/edit")
    @ApiOperation(value="管理员编辑")
    public AjaxResult<Object> edit(@Validated @RequestBody SystemAdminUpdateValidate updateValidate) {
        Integer adminId = AdminThreadLocal.getAdminId();
        iSystemAuthAdminService.edit(updateValidate, adminId);
        return AjaxResult.success();
    }

    @NotPower
    @Log(title = "管理员更新")
    @PostMapping("/upInfo")
    @ApiOperation(value="当前管理员更新")
    public AjaxResult<Object> upInfo(@Validated @RequestBody SystemAdminUpInfoValidate upInfoValidate) {
        Integer adminId = AdminThreadLocal.getAdminId();
        iSystemAuthAdminService.upInfo(upInfoValidate, adminId);
        return AjaxResult.success();
    }

    @Log(title = "管理员删除")
    @PostMapping("/delete")
    @ApiOperation(value="管理员删除")
    public AjaxResult<Object> del(@Validated @RequestBody IdValidate idValidate) {
        Integer adminId = AdminThreadLocal.getAdminId();
        iSystemAuthAdminService.del(idValidate.getId(), adminId);
        return AjaxResult.success();
    }

    @Log(title = "管理员状态")
    @PostMapping("/disable")
    @ApiOperation(value="管理员状态切换")
    public AjaxResult<Object> disable(@Validated @RequestBody IdValidate idValidate) {
        Integer adminId = AdminThreadLocal.getAdminId();
        iSystemAuthAdminService.disable(idValidate.getId(), adminId);
        return AjaxResult.success();
    }

}
