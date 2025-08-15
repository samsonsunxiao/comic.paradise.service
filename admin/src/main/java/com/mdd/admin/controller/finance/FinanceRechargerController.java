package com.mdd.admin.controller.finance;

import com.alibaba.fastjson2.JSONObject;
import com.mdd.admin.AdminThreadLocal;
import com.mdd.admin.service.IFinanceRechargerService;
import com.mdd.admin.validate.commons.IdValidate;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.finance.FinanceRechargeSearchValidate;
import com.mdd.admin.validate.finance.FinanceRefundValidate;
import com.mdd.admin.vo.finance.FinanceRechargeListVo;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import com.mdd.common.enums.ErrorEnum;
import com.mdd.common.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping({"/adminapi/recharge/recharge", "/adminapi/recharge.recharge"})
@Api("充值记录管理")
public class FinanceRechargerController {

    @Resource
    IFinanceRechargerService iFinanceRechargerService;

    @GetMapping("/lists")
    @ApiOperation("充值记录")
    public AjaxResult<Object> list(@Validated PageValidate pageValidate,
                                   @Validated FinanceRechargeSearchValidate searchValidate) {
        if (StringUtils.isNotNull(searchValidate.getExport()) && searchValidate.getExport().equals(1)) {
            JSONObject ret = iFinanceRechargerService.getExportData(pageValidate, searchValidate);
            return AjaxResult.success(ret);
        } else if (StringUtils.isNotNull(searchValidate.getExport()) && searchValidate.getExport().equals(2)) {
            String path = iFinanceRechargerService.export(searchValidate);
            return AjaxResult.success(2, new JSONObject() {{
                put("url", path);
            }}, ErrorEnum.SHOW_MSG.getCode());
        } else {
            PageResult<FinanceRechargeListVo> list = iFinanceRechargerService.list(pageValidate, searchValidate);
            return AjaxResult.success(list);
        }
    }

    @PostMapping("/refund")
    @ApiOperation("发起退款")
    public AjaxResult<Object> refund(@Validated @RequestBody FinanceRefundValidate financeRefundValidate) {
        Integer adminId = AdminThreadLocal.getAdminId();

        iFinanceRechargerService.refund(financeRefundValidate.getRecharge_id(), adminId, financeRefundValidate.getRefundRate());
        return AjaxResult.success();
    }

    @PostMapping("/refundAgain")
    @ApiModelProperty("重新退款")
    public AjaxResult<Object> refundAgain(@Validated @RequestBody IdValidate idValidate) {
        Integer adminId = AdminThreadLocal.getAdminId();

        iFinanceRechargerService.refundAgain(idValidate.getId(), adminId);
        return AjaxResult.success();
    }

}
