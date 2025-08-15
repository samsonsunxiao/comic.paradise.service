package com.mdd.admin.controller.game;

import com.mdd.admin.service.IGameService;
import com.mdd.admin.validate.game.GameSaveValidate;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.game.GameSearchValidate;
import com.mdd.admin.vo.game.GameDetailVo;
import com.mdd.admin.vo.game.GameListVo;
import com.mdd.admin.vo.game.GameNameVo;
import com.mdd.common.aop.NotLogin;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import com.mdd.common.util.ConfigUtils;
import com.mdd.common.validator.annotation.IDMust;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("adminapi/game")
@Api(tags = "游戏 资源管理")
public class GameController {

    @Resource
    @Autowired
    IGameService iGameService;

    //@Resource
    //AliOSSService aliOSSService;
    @NotLogin
    @GetMapping("/all")
    @ApiOperation(value="资源列表")
    public AjaxResult<List<GameNameVo>> all(@Validated @IDMust() @RequestParam("status") String status) {
        List<GameNameVo> listGame = iGameService.all(status);
        return AjaxResult.success(listGame);
    }
    @NotLogin
    @GetMapping("/list")
    @ApiOperation(value="资源列表")
    public AjaxResult<PageResult<GameListVo>> list(@Validated PageValidate pageValidate,
                                                     @Validated GameSearchValidate searchValidate) {
        PageResult<GameListVo> listGame = iGameService.list(pageValidate, searchValidate);
        return AjaxResult.success(listGame);
    }
    @NotLogin
    @GetMapping("/detail")
    @ApiOperation(value="详情")
    public AjaxResult<GameDetailVo> detail(@Validated @IDMust() @RequestParam("gid") String gid) {
        GameDetailVo detailGame = iGameService.detail(gid);
        return AjaxResult.success(detailGame);
    }
    @NotLogin
    @PostMapping("/save")
    @ApiOperation(value="保存")
    public AjaxResult<Object> save(@Validated @RequestBody GameSaveValidate saveValidate) {
        iGameService.save(saveValidate);
        return AjaxResult.success();
    }
    @NotLogin
    @PostMapping("/keyurivalid")
    @ApiOperation(value="检验keyuri")
    public AjaxResult<Object> checkKeyuri(@Validated @RequestBody String keyUri) {
        // Map<String, Object> oss = aliOSSService.getSts();
        // Map<String, String> websiteConfig = ConfigUtils.get("website");
        // String strAliBucket = websiteConfig.getOrDefault("aliBucket", "");
        // String strAliRegion = websiteConfig.getOrDefault("aliRegion", "");
        // oss.put("bucket", strAliBucket);
        // oss.put("region", strAliRegion);
        Map<String, Object> res = new HashMap<>();
        //res.put("oss", oss);
        //res.put("existed", iGameService.checkKeyUriExisted(keyUri));
        return AjaxResult.success(res);
    }
}
