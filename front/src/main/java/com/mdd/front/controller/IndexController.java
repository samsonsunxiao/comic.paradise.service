package com.mdd.front.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mdd.common.aop.NotLogin;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.entity.notice.NoticeRecord;
import com.mdd.common.enums.NoticeEnum;
import com.mdd.common.exception.OperateException;
import com.mdd.common.mapper.notice.NoticeRecordMapper;
import com.mdd.common.plugin.notice.NoticeDriver;
import com.mdd.common.plugin.notice.vo.NoticeSmsVo;
import com.mdd.common.util.StringUtils;
import com.mdd.common.util.ToolUtils;
import com.mdd.common.validator.annotation.IDMust;
import com.mdd.front.service.IIndexService;
import com.mdd.front.validate.common.SmsValidate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/index")
@Api(tags = "主页管理")
public class IndexController {

    @Resource
    NoticeRecordMapper noticeRecordMapper;

    @Resource
    IIndexService iIndexService;

    @NotLogin
    @GetMapping("/index")
    @ApiOperation(value="首页数据")
    public AjaxResult<Map<String, Object>> index() {
        Map<String, Object> detail = iIndexService.index();
        return AjaxResult.success(detail);
    }

    @NotLogin
    @GetMapping("/decorate")
    @ApiOperation(value="装修数据")
    public AjaxResult<Map<String, Object>> decorate(@Validated @IDMust() @RequestParam("id") Integer id) {
        Map<String, Object> detail = iIndexService.decorate(id);
        return AjaxResult.success(detail);
    }

    @NotLogin
    @GetMapping("/config")
    @ApiOperation(value="公共配置")
    public AjaxResult<Map<String, Object>> config() {
        Map<String, Object> map = iIndexService.config();
        return AjaxResult.success(map);
    }

    @NotLogin
    @GetMapping("/policy")
    @ApiOperation(value="政策协议")
    public AjaxResult<Map<String, String>> policy(@RequestParam String type) {
        Map<String, String> map = iIndexService.policy(type);
        return AjaxResult.success(map);
    }

    @NotLogin
    @GetMapping("/hotSearch")
    @ApiOperation(value="热门搜索")
    public AjaxResult<List<String>> hotSearch() {
        List<String> list = iIndexService.hotSearch();
        return AjaxResult.success(list);
    }
}
