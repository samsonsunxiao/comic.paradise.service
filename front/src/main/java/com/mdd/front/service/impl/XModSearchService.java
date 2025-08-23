package com.mdd.front.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.game.GameInfo;
import com.mdd.common.entity.mod.XMod;
import com.mdd.common.mapper.xmod.XModMapper;
import com.mdd.common.mapper.comic.ArticleMapper;
import com.mdd.common.mapper.xmod.XModImagesMapper;
import com.mdd.front.service.IXModSearch;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.vo.xmod.ModSummaryVo;
import com.mdd.front.vo.xmod.XModSuggestVo;

import java.util.*;

@Service
public class XModSearchService implements IXModSearch {
    @Autowired
    ArticleMapper gameMapper;

    @Autowired
    XModMapper modMapper;

    @Autowired
    XModImagesMapper imagesMapper;

    @Override
    public List<XModSuggestVo> suggest(String keyword){
        //查找游戏
        MPJQueryWrapper<GameInfo> mpjQueryWrapper = new MPJQueryWrapper<GameInfo>()
            .selectAll(GameInfo.class)
            .like("t.name", keyword)
            .eq("t.status", "online")
            .orderByDesc("t.score");
        IPage<GameInfo> iPage = gameMapper.selectJoinPage(
                new Page<>(0, 10),
                GameInfo.class,
                mpjQueryWrapper);
        List<XModSuggestVo> list = new LinkedList<>();
        for (GameInfo item : iPage.getRecords()) {
            XModSuggestVo vo = new XModSuggestVo();
            vo.setImage(item.getCoverImage());
            vo.setName(item.getName());
            vo.setUid(item.getGid());
            vo.setType("game");
            list.add(vo);
        }
        //查找MOD
        MPJQueryWrapper<XMod> mpjQueryWrapper1 = new MPJQueryWrapper<XMod>()
            .select("MIN(t.id) as id, MIN(t.title) as name, t.modid as modid, MIN(img.url) as image")
            .innerJoin("xmod_images img ON t.modid=img.uid")
            .groupBy("t.modid")
            .like("t.title", keyword)
            .orderByDesc("t.hot")
            .eq("t.status", "online");
        IPage<ModSummaryVo> iPage1 = modMapper.selectJoinPage(
                new Page<>(0, 10),
                ModSummaryVo.class,
                mpjQueryWrapper1);    
        for (ModSummaryVo item : iPage1.getRecords()) {
            XModSuggestVo vo = new XModSuggestVo();
            vo.setImage(item.getImage());
            vo.setName(item.getName());
            vo.setUid(item.getModid());
            vo.setType("mod");
            list.add(vo);
        }
        return list;
    }
}
