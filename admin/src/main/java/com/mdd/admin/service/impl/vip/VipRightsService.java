package com.mdd.admin.service.impl.vip;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.admin.service.vip.IVipRightsService;
import com.mdd.admin.validate.vip.RightsSaveValidate;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.vip.LevelRights;
import com.mdd.common.entity.vip.VipRights;
import com.mdd.common.mapper.vip.LevelRightsMapper;
import com.mdd.common.mapper.vip.VipRightsMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VipRightsService implements IVipRightsService{
    @Resource
    @Autowired
    VipRightsMapper vipRightsMapper;

    @Resource
    @Autowired
    LevelRightsMapper levelRightsMapper;
 
    public List<VipRights> all() {
        List<VipRights> listModule  = vipRightsMapper.selectList(new QueryWrapper<VipRights>());
        return listModule;
    }
     /**
     * Vip 权益列表
     *
     * @author 
     */
    public PageResult<VipRights> list(){
        MPJQueryWrapper<VipRights> mpjQueryWrapper = new MPJQueryWrapper<VipRights>()
            .selectAll(VipRights.class);
        IPage<VipRights> iPage = vipRightsMapper.selectJoinPage(
                new Page<>(0, -1),
                VipRights.class,
                mpjQueryWrapper);
        return PageResult.iPageHandle(iPage);
    }

    public VipRights detail(String key) {
        VipRights model = vipRightsMapper.selectOne(
            new QueryWrapper<VipRights>().eq("keyid", key)
        );
        return model;
    }

    public void save(RightsSaveValidate rightsSaveValidate)
    {
        VipRights model = vipRightsMapper.selectOne(
            new QueryWrapper<VipRights>().eq("keyid", rightsSaveValidate.getKeyid())
        );
        Boolean isNew = false;
        if (model == null){
            model = new VipRights();
            isNew = true;
            long number =  100 + vipRightsMapper.selectCount(new QueryWrapper<VipRights>()) + 1;
            StringBuilder sb = new StringBuilder();
            sb.append("R");
            sb.append(String.format("%02d", number % 100));
            model.setKeyid(sb.toString());
        }
       
        model.setTitle(rightsSaveValidate.getTitle());
        model.setDescript(rightsSaveValidate.getDescript());
        model.setValue(rightsSaveValidate.getValue());
        model.setIcon(rightsSaveValidate.getIcon());
        model.setType(rightsSaveValidate.getType());
        if (isNew){
            vipRightsMapper.insert(model);
        }
        else{
            vipRightsMapper.updateById(model);
        }
    }

    public void del(Integer id) {
        VipRights model = vipRightsMapper.selectOne(
                new QueryWrapper<VipRights>()
                        .eq("id", id));
        Assert.notNull(model, "权益不存在");
        //先删除有关系的的等级
        levelRightsMapper.delete(new QueryWrapper<LevelRights>().eq("rights", model.getKeyid()));
        vipRightsMapper.deleteById(id);
    }
}
