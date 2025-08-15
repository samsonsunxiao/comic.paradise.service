package com.mdd.front.controller;

import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import com.mdd.front.FrontThreadLocal;
import com.mdd.front.service.IUserAccountLogService;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.validate.users.UserAccountLogSearchValidate;
import com.mdd.front.vo.user.UserAccountListVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/account_log")
@Api(tags = "用户资金变更管理")
public class AccountLogController {

    @Resource
    IUserAccountLogService iUserAccountLogService;

    @GetMapping("/lists")
    @ApiOperation(value="用户资金变更列表")
    public AjaxResult<PageResult<UserAccountListVo>> lists(@Validated PageValidate pageValidate,
                                                           @Validated UserAccountLogSearchValidate searchValidate) {
        searchValidate.setUserId(FrontThreadLocal.getUserId());
        PageResult<UserAccountListVo> list = iUserAccountLogService.lists(pageValidate, searchValidate);
        return AjaxResult.success(list);
    }
}
