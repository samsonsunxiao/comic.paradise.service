package com.mdd.front.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.game.GameInfo;
import com.mdd.common.entity.game.XModuleGame;
import com.mdd.common.entity.mod.XMod;
import com.mdd.common.exception.OperateException;
import com.mdd.common.mapper.xmod.XModMapper;
import com.mdd.common.mapper.comic.ArticleMapper;
import com.mdd.common.mapper.xmod.ModuleGameMapper;
import com.mdd.common.mapper.xmod.XModImagesMapper;
import com.mdd.common.util.ConfigUtils;
import com.mdd.common.util.MapUtils;
import com.mdd.common.util.TimeUtils;
import com.mdd.front.service.IGameService;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.validate.xmod.XModGameSearchValidate;
import com.mdd.front.vo.xmod.GameDetailVo;
import com.mdd.front.vo.xmod.GameSummaryVo;
import com.mdd.front.vo.xmod.ModSummaryVo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

@Service
public class GameService implements IGameService {
    @Autowired
    ArticleMapper gameMapper;

    @Autowired
    XModMapper modMapper;

    @Autowired
    XModImagesMapper imagesMapper;

    @Autowired
    ModuleGameMapper moduleGameMapper;

    @Override
    public Map<String, Map<String, Object>> list(PageValidate pageValidate, XModGameSearchValidate searchValidate) {
        Map<String, Map<String, Object>> mapGame = new HashMap<>();
        Map<String, Object> mapParam = MapUtils.jsonToMapAsObj(searchValidate.getParam());
        mapParam.forEach((key, value) -> {
            MPJQueryWrapper<XModuleGame> mpjQueryWrapper = new MPJQueryWrapper<XModuleGame>()
                    .selectAll(XModuleGame.class)
                    .select("g.name as name,g.cover_image as image, g.score as score, m.name as modulename, g.online_time as onlineTime, g.banner as banner")
                    .innerJoin("xmod_games g ON t.gid=g.gid")
                    .innerJoin("xmod_modules m ON t.moduleid=m.moduleid")
                    .eq("t.moduleid", key)
                    .orderByDesc("g.score");
            IPage<GameSummaryVo> iPage = moduleGameMapper.selectJoinPage(
                    new Page<>(0, -1),
                    GameSummaryVo.class,
                    mpjQueryWrapper);
            String moduleName = "";
            for (GameSummaryVo item : iPage.getRecords()) {
                moduleName = item.getModulename();
                item.setOnlineTime(TimeUtils.timestampToDate(item.getOnlineTime(), "yyyy-MM-dd"));
            }
            Map<String, Object> mapModule = new HashMap<>();
            mapModule.put("name", moduleName);
            mapModule.put("list", iPage.getRecords());
            mapGame.put(key, mapModule);
        });
        return mapGame;
    }

    public PageResult<GameSummaryVo> listStore(PageValidate pageValidate, XModGameSearchValidate searchValidate) {
        Map<String, String> mapParam = MapUtils.jsonToMap(searchValidate.getParam());
        Integer pageNo = pageValidate.getPage_no();
        Integer pageSize = pageValidate.getPage_size();
        MPJQueryWrapper<GameInfo> mpjQueryWrapper = new MPJQueryWrapper<GameInfo>()
                .select("t.name as name, t.gid as gid, t.score as score, t.cover_image as image, t.online_time as onlineTime, COUNT(mods.modid) AS modcount")
                .leftJoin("xmod_store mods ON t.gid=mods.gid")
                .groupBy("t.gid,t.name, t.cover_image,t.online_time,t.score")
                .eq("t.status", "online");
        if (mapParam.get("key").equals("score")) {
            if (mapParam.get("sort").equals("desc")) {
                mpjQueryWrapper.orderByDesc("score");
            } else {
                mpjQueryWrapper.orderByAsc("score");
            }
        } else if (mapParam.get("key").equals("date")) {
            if (mapParam.get("sort").equals("desc")) {
                mpjQueryWrapper.orderByDesc("onlineTime");
            } else {
                mpjQueryWrapper.orderByAsc("onlineTime");
            }
        } else if (mapParam.get("key").equals("modcount")) {
            if (mapParam.get("sort").equals("desc")) {
                mpjQueryWrapper.orderByDesc("modcount");
            } else {
                mpjQueryWrapper.orderByAsc("modcount");
            }
        }
        if (!mapParam.get("keyword").isEmpty()) {
            mpjQueryWrapper.nested(wq -> wq.like("t.name", mapParam.get("keyword")));
        }
        IPage<GameSummaryVo> iPage = gameMapper.selectJoinPage(
                new Page<>(pageNo, pageSize),
                GameSummaryVo.class,
                mpjQueryWrapper);
        for (GameSummaryVo item : iPage.getRecords()) {
            item.setOnlineTime(TimeUtils.timestampToDate(item.getOnlineTime()));
        }
        return PageResult.iPageHandle(iPage);
    }

    @Override
    public GameDetailVo detail(String gid) {
        GameInfo model = gameMapper.selectOne(
                new QueryWrapper<GameInfo>()
                        .eq("gid", gid));
        Assert.notNull(model, "游戏不存在");
        GameDetailVo vo = new GameDetailVo();
        BeanUtils.copyProperties(model, vo);
        MPJQueryWrapper<XMod> mpjQueryWrapper = new MPJQueryWrapper<XMod>()
                .select("MIN(t.id) as id, MIN(t.title) as name, t.modid as modid, MIN(t.vip) as vip, MIN(t.hot) as hot, MIN(t.downloadcount) as downloadcount, MIN(t.online_time) as onlineTime, MIN(img.url) as image, MIN(file.size) as size")
                .leftJoin("xmod_images img ON t.modid=img.uid and img.status='online'")
                .leftJoin("xmod_files file ON t.modid=file.uid and file.type='combin' and file.status='online'")
                .groupBy("t.modid")
                .eq("gid", gid)
                .eq("t.status", "online");
        IPage<ModSummaryVo> iPage = modMapper.selectJoinPage(
                new Page<>(1, -1),
                ModSummaryVo.class,
                mpjQueryWrapper);
        for (ModSummaryVo item : iPage.getRecords()) {
            if (!item.getOnlineTime().isEmpty()) {
                item.setOnlineTime(TimeUtils.timestampToDate(item.getOnlineTime(), "yyyy-MM-dd"));
            }
        }
        vo.setImage(model.getCoverImage());
        vo.setMods(iPage.getRecords());
        MPJQueryWrapper<XModuleGame> mpjQueryWrapper1 = new MPJQueryWrapper<XModuleGame>()
                .selectAll(XModuleGame.class)
                .select("g.gid as gid, g.name as name,g.cover_image as image, g.score as score, m.name as modulename, g.online_time as onlineTime, g.banner as banner")
                .innerJoin("xmod_games g ON t.gid=g.gid")
                .innerJoin("xmod_modules m ON t.moduleid=m.moduleid")
                .eq("t.moduleid", "suggest")
                .orderByDesc("g.score");
        IPage<GameSummaryVo> iPage1 = moduleGameMapper.selectJoinPage(
                new Page<>(0, -1),
                GameSummaryVo.class,
                mpjQueryWrapper1);
        vo.setRecommands(iPage1.getRecords());
        return vo;
    }
}
