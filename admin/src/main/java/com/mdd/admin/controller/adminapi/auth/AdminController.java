package com.mdd.admin.controller.adminapi.auth;

import com.mdd.admin.AdminThreadLocal;
import com.mdd.admin.service.IIndexService;
import com.mdd.admin.service.admin.IAdminRoleService;
import com.mdd.admin.service.admin.IAdminService;
import com.mdd.admin.vo.auth.AdminMySelfVo;
import com.mdd.common.aop.NotLogin;
import com.mdd.common.aop.NotPower;
import com.mdd.common.core.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping({"adminapi/auth/admin", "adminapi/auth.admin"})
@Api(tags = "管理员详情管理")
public class AdminController {

    @Resource
    IAdminService iAdminService;

    @Resource
    IAdminRoleService iAdminRoleService;

    @NotPower
    @GetMapping("/mySelf")
    @ApiOperation(value="获取当前管理员信息")
    public AjaxResult<AdminMySelfVo> mySelf() {
        Integer adminId = AdminThreadLocal.getAdminId();

        List<Integer> roleIds = iAdminRoleService.getRoleIdAttr(adminId);

        AdminMySelfVo mySelf = iAdminService.mySelf(AdminThreadLocal.getAdminId(), roleIds);
        return AjaxResult.success(mySelf);
    }

}
