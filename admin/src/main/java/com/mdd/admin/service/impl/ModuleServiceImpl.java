package com.mdd.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.admin.service.IModuleService;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.module.XModuleSaveValidate;
import com.mdd.common.core.PageResult;
import com.mdd.common.mapper.xmod.ModuleGameMapper;
import com.mdd.common.mapper.xmod.ModuleMapper;

import com.mdd.admin.vo.module.XModuleDetailVo;
import com.mdd.admin.vo.module.XModuleListVo;
import com.mdd.admin.vo.game.GameNameVo;
import com.mdd.common.entity.module.XModule;
import com.mdd.common.entity.module.XModuleGameVo;
import com.mdd.common.entity.game.XModuleGame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import javax.annotation.Resource;

import java.util.*;
import java.util.stream.Collectors;

/**
 * MOD服务实现类
 */
@Slf4j
@Service
public class ModuleServiceImpl implements IModuleService {

    @Resource
    @Autowired
    ModuleMapper moduleMapper;
    
    @Autowired
    ModuleGameMapper moduleGameMapper;

     /**
     * 所有
     *
     * @author fzr
     * @return List<GameNameVo>
     */
    @Override
    public List<XModule> all() {
        List<XModule> listModule  = moduleMapper.selectList(new QueryWrapper<XModule>());
        return listModule;
    }
    
    /**
     * 列表
     *
     * @param pageValidate   分页参数
     * @param searchValidate 搜索参数
     * @return PageResult<ResourceListedVo>
     * @author fzr
     */
    @Override
    public PageResult<XModuleListVo> list(PageValidate pageValidate) {
        Integer pageNo = pageValidate.getPage_no();
        Integer pageSize = pageValidate.getPage_size();
        MPJQueryWrapper<XModule> mpjQueryWrapper = new MPJQueryWrapper<XModule>()
                .selectAll(XModule.class)
                .orderByDesc("t.id");
        IPage<XModule> iPage = moduleMapper.selectJoinPage(
                new Page<>(pageNo, pageSize),
                XModule.class,
                mpjQueryWrapper);
        List<XModuleListVo> listModuleListVos = new ArrayList<>();
        for(XModule item : iPage.getRecords()){
            XModuleListVo vo = new XModuleListVo();
            BeanUtils.copyProperties(item, vo);
            Long gameCount = moduleGameMapper.selectCount(new
                QueryWrapper<XModuleGame>().eq("moduleid", item.getModuleid()));
            if (gameCount != null){
                vo.setGameCount(gameCount);
            }
            listModuleListVos.add(vo);
        }
        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), listModuleListVos);
    }

    /**
     * 资源详情
     *
     * @param id 主键ID
     * @author fzr
     */
    @Override
    public XModuleDetailVo detail(String moduleid){
        XModule model = moduleMapper.selectOne(
                new QueryWrapper<XModule>()
                        .eq("moduleid", moduleid));
        Assert.notNull(model, "模块不存在");
        MPJQueryWrapper<XModuleGame> mpjQueryWrapper = new MPJQueryWrapper<XModuleGame>()
            .selectAll(XModuleGame.class)
            .select("g.name as game,g.cover_image as image")
            .innerJoin("xmod_games g ON t.gid=g.gid")
            .eq("t.moduleid", moduleid);
        IPage<XModuleGameVo> iPage = moduleGameMapper.selectJoinPage(
                new Page<>(0, -1),
                XModuleGameVo.class,
                mpjQueryWrapper);
        List<GameNameVo> listGames = new ArrayList<>();
        for (XModuleGameVo item : iPage.getRecords()) {
            GameNameVo gameVo = new GameNameVo();
            BeanUtils.copyProperties(item, gameVo);
            gameVo.setName(item.getGame());
            listGames.add(gameVo);
        }
        XModuleDetailVo moduleDetailVo = new XModuleDetailVo();
        BeanUtils.copyProperties(model, moduleDetailVo);
        moduleDetailVo.setGames(listGames);
        return moduleDetailVo;
    }

    public void save(XModuleSaveValidate saveValidate){
        XModule model = moduleMapper.selectOne(
                new QueryWrapper<XModule>()
                        .eq("moduleid", saveValidate.getModuleid()));
        Boolean isNew = false;
        if (model == null){
            model = new XModule();
            isNew = true;
        }
        model.setModuleid(saveValidate.getModuleid());
        model.setName(saveValidate.getName());
        if (isNew){
            moduleMapper.insert(model);
        }
        else{
            moduleMapper.updateById(model);
        }
        List<XModuleGame> listModuleGame = moduleGameMapper.selectList(new
            QueryWrapper<XModuleGame>().eq("moduleid", saveValidate.getModuleid()));
        List<String> gidList = listModuleGame.stream().map(XModuleGame::getGid).collect(Collectors.toList());
        List<String> listAdd = saveValidate.getGames().stream()
            .filter(gid1 -> gidList.stream().noneMatch(gid2 -> gid1.equals(gid2))).collect(Collectors.toList());
        for (String gid : listAdd) {
            XModuleGame moduleGame = new XModuleGame();
            moduleGame.setGid(gid);
            moduleGame.setModuleid(saveValidate.getModuleid());
            moduleGameMapper.insert(moduleGame);
        }
        List<String> listDel = gidList.stream()
            .filter(gid1 -> saveValidate.getGames().stream().noneMatch(gid2 -> gid1.equals(gid2))).collect(Collectors.toList());
        for (String gid : listDel) {
            moduleGameMapper.delete(new QueryWrapper<XModuleGame>().eq("gid", gid).eq("moduleid", saveValidate.getModuleid()));
        }
    }
    public void del(Integer id) {
        //先删除有关系的游戏
        XModule model = moduleMapper.selectOne(
                new QueryWrapper<XModule>()
                        .eq("id", id));
        Assert.notNull(model, "模块不存在");
        moduleGameMapper.delete(new QueryWrapper<XModuleGame>().eq("moduleid", model.getModuleid()));
        moduleMapper.deleteById(id);
    }
}
