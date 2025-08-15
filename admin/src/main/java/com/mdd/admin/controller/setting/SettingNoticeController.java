package com.mdd.admin.controller.setting;

import com.alibaba.fastjson2.JSONObject;
import com.mdd.admin.aop.Log;
import com.mdd.admin.service.ISettingNoticeService;
import com.mdd.admin.vo.setting.SettingNoticeDetailVo;
import com.mdd.admin.vo.setting.SettingNoticeListedVo;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/adminapi/notice.notice")
@Api(tags = "配置消息通知")
public class SettingNoticeController {

    @Resource
    ISettingNoticeService iSettingNoticeService;

    @GetMapping("/settingLists")
    @ApiOperation(value="通知设置列表")
    public AjaxResult<JSONObject> list(@RequestParam Integer recipient) {
        List<SettingNoticeListedVo> list = iSettingNoticeService.list(recipient);
        JSONObject result = new JSONObject();
        result.put("lists", list);
        return AjaxResult.success(result);
    }

    @GetMapping("/detail")
    @ApiOperation(value="通知设置详情")
    public AjaxResult<SettingNoticeDetailVo> detail(@Validated @IDMust() @RequestParam("id") Integer id) {
        SettingNoticeDetailVo vo = iSettingNoticeService.detail(id);
        return AjaxResult.success(vo);
    }

    @Log(title = "通知设置编辑")
    @PostMapping("/set")
    @ApiOperation(value="通知设置编辑")
    public AjaxResult<Object> save(@RequestBody JSONObject params) {
        iSettingNoticeService.save(params);
        return AjaxResult.success();
    }

}
