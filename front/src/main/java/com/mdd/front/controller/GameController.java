package com.mdd.front.controller;

import com.mdd.common.aop.NotLogin;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.validator.annotation.IDMust;
import com.mdd.front.service.IGameService;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.validate.xmod.XModGameSearchValidate;
import com.mdd.front.vo.xmod.GameDetailVo;
import com.mdd.front.vo.xmod.GameSummaryVo;
import com.mdd.common.core.PageResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/game")
@Api(tags = "XMOD api")
public class GameController {
    @Resource
    IGameService iGameService;
    
    @NotLogin
    @PostMapping("/list")
    @ApiOperation(value="游戏查询")
    public AjaxResult<Map<String, Map<String, Object>>> list(@Validated PageValidate pageValidate, XModGameSearchValidate searchValidate) {
        Map<String, Map<String, Object>> mapResond = iGameService.list(pageValidate, searchValidate);
        return AjaxResult.success(mapResond);
    }

    @NotLogin
    @PostMapping("/store")
    @ApiOperation(value="游戏查询")
    public AjaxResult<PageResult<GameSummaryVo>> store(@Validated PageValidate pageValidate, XModGameSearchValidate searchValidate) {
        PageResult<GameSummaryVo> mapResond = iGameService.listStore(pageValidate, searchValidate);
        return AjaxResult.success(mapResond);
    }
    
    @NotLogin
    @PostMapping("/detail")
    @ApiOperation(value="游戏详情")
    public AjaxResult<GameDetailVo> detail(@Validated @IDMust() @RequestParam("gid") String gid) {
        GameDetailVo detail = iGameService.detail(gid);
        return AjaxResult.success(detail);
    }
}
