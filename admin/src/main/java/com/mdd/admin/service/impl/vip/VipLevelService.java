package com.mdd.admin.service.impl.vip;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.admin.service.vip.IVipLevelService;
import com.mdd.admin.validate.vip.LevelSaveValidate;
import com.mdd.common.entity.vip.VipLevel;
import com.mdd.common.entity.vip.VipLevelDetailVo;
import com.mdd.common.entity.vip.VipRights;
import com.mdd.common.entity.vip.LevelRights;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.vip.LevelPay;
import com.mdd.common.entity.vip.PayModel;
import com.mdd.common.entity.vip.LevelRightsVo;
import com.mdd.common.entity.vip.LevelPayVo;
import com.mdd.common.mapper.vip.VipLevelMapper;
import com.mdd.common.mapper.vip.LevelRightsMapper;
import com.mdd.common.mapper.vip.LevelPayMapper;
import com.mdd.common.mapper.vip.VipRightsMapper;
import com.mdd.common.mapper.vip.PaymodelMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VipLevelService implements IVipLevelService {
    @Resource
    @Autowired
    VipLevelMapper vipLevelMapper;

    @Resource
    @Autowired
    LevelRightsMapper levelRightsMapper;

    @Resource
    @Autowired
    LevelPayMapper levelPayMapper;

    @Resource
    @Autowired
    VipRightsMapper vipRightsMapper;

    @Resource
    @Autowired
    PaymodelMapper paymodelMapper;

    public List<VipLevel> all() {
        List<VipLevel> listLevels = vipLevelMapper.selectList(new QueryWrapper<VipLevel>());
        return listLevels;
    }

    public PageResult<VipLevel> list(){
        MPJQueryWrapper<VipLevel> mpjQueryWrapper = new MPJQueryWrapper<VipLevel>()
            .selectAll(VipLevel.class);
        IPage<VipLevel> iPage = vipLevelMapper.selectJoinPage(
                new Page<>(0, -1),
                VipLevel.class,
                mpjQueryWrapper);
        return PageResult.iPageHandle(iPage);
    }

    public VipLevelDetailVo detail(String keyid) {
        VipLevel model = vipLevelMapper.selectOne(
                new QueryWrapper<VipLevel>()
                        .eq("keyid", keyid));
        Assert.notNull(model, "等级不存在");
        // 权益列表
        MPJQueryWrapper<LevelRights> mpjQueryWrapper = new MPJQueryWrapper<LevelRights>()
                .selectAll(LevelRights.class)
                .select("r.descript as descript")
                .innerJoin("xmod_vip_rights r ON r.keyid=t.rights")
                .eq("t.level", keyid);
        IPage<LevelRightsVo> iPage = levelRightsMapper.selectJoinPage(
                new Page<>(0, -1),
                LevelRightsVo.class,
                mpjQueryWrapper);

        List<VipRights> listRights = new ArrayList<>();
        for (LevelRightsVo item : iPage.getRecords()) {
            VipRights rights = new VipRights();
            rights.setDescript(item.getDescript());
            rights.setKeyid(item.getRights());
            listRights.add(rights);
        }
        // 支付模式列表
        MPJQueryWrapper<LevelPay> mpjQueryWrapper1 = new MPJQueryWrapper<LevelPay>()
                .selectAll(LevelPay.class)
                .select("p.title as title, p.price as price")
                .innerJoin("xmod_pay_model p ON p.keyid=t.paymodel")
                .eq("t.level", keyid);
        IPage<LevelPayVo> iPage1 = levelPayMapper.selectJoinPage(
                new Page<>(0, -1),
                LevelPayVo.class,
                mpjQueryWrapper1);

        List<PayModel> listPays = new ArrayList<>();
        for (LevelPayVo item : iPage1.getRecords()) {
            PayModel payModel = new PayModel();
            payModel.setTitle(item.getTitle());
            payModel.setKeyid(item.getPaymodel());
            listPays.add(payModel);
        }
        VipLevelDetailVo vo = new VipLevelDetailVo();
        BeanUtils.copyProperties(model, vo);
        vo.setRights(listRights);
        vo.setPays(listPays);
        return vo;
    }

    public void save(LevelSaveValidate levelSaveValidate) {
        VipLevel model = vipLevelMapper.selectOne(
                new QueryWrapper<VipLevel>().eq("keyid", levelSaveValidate.getKeyid()));
        Boolean isNew = false;
        if (model == null){
            model = new VipLevel();
            isNew = true;
            long number =  100 + vipLevelMapper.selectCount(new QueryWrapper<VipLevel>()) + 1;
            StringBuilder sb = new StringBuilder();
            sb.append("V");
            sb.append(String.format("%02d", number % 100));
            model.setKeyid(sb.toString());
        }
        model.setTitle(levelSaveValidate.getTitle());
        model.setValue(levelSaveValidate.getValue());
        if (isNew){
            vipLevelMapper.insert(model);
        }
        else{
            vipLevelMapper.updateById(model);
        }
        // 等级权益关联
        List<String> rightsNew = levelSaveValidate.getRights();
        List<LevelRights> listLevelRights = levelRightsMapper
                .selectList(new QueryWrapper<LevelRights>().eq("level", levelSaveValidate.getKeyid()));
        List<String> rightsList = listLevelRights.stream().map(LevelRights::getRights).collect(Collectors.toList());
        List<String> listRightsAdd = rightsNew.stream()
                .filter(r1 -> rightsList.stream().noneMatch(r2 -> r1.equals(r2))).collect(Collectors.toList());
        for (String item : listRightsAdd) {
            LevelRights levelRights = new LevelRights();
            levelRights.setLevel(levelSaveValidate.getKeyid());
            levelRights.setRights(item);
            levelRightsMapper.insert(levelRights);
        }
        List<String> listRightsDel = rightsList.stream()
            .filter(r1 -> rightsNew.stream().noneMatch(r2 -> r1.equals(r2))).collect(Collectors.toList());
        for (String item : listRightsDel) {
            levelRightsMapper.delete(new QueryWrapper<LevelRights>().eq("level", levelSaveValidate.getKeyid()).eq("rights", item));
        }
        // 等级支付模式关联
        List<String> paysNew = levelSaveValidate.getPays();
        List<LevelPay> listLevelPay = levelPayMapper
                .selectList(new QueryWrapper<LevelPay>().eq("level", levelSaveValidate.getKeyid()));
        List<String> payList = listLevelPay.stream().map(LevelPay::getPaymodel).collect(Collectors.toList());
        List<String> listPayAdd = paysNew.stream()
                .filter(p1 -> payList.stream().noneMatch(p2 -> p1.equals(p2))).collect(Collectors.toList());
        for (String item : listPayAdd) {
            LevelPay levelPay = new LevelPay();
            levelPay.setLevel(levelSaveValidate.getKeyid());
            levelPay.setPaymodel(item);
            levelPayMapper.insert(levelPay);
        }
        List<String> listPayDel = payList.stream()
            .filter(p1 -> paysNew.stream().noneMatch(p2 -> p1.equals(p2))).collect(Collectors.toList());
        for (String item : listPayDel) {
            levelPayMapper.delete(new QueryWrapper<LevelPay>().eq("level", levelSaveValidate.getKeyid()).eq("paymodel", item));
        }
    }

    public void del(Integer id) {
        VipLevel model = vipLevelMapper.selectOne(
                new QueryWrapper<VipLevel>()
                        .eq("id", id));
        Assert.notNull(model, "等级不存在");
        //先删除有关系的的等级
        levelRightsMapper.delete(new QueryWrapper<LevelRights>().eq("level", model.getKeyid()));
        levelPayMapper.delete(new QueryWrapper<LevelPay>().eq("level", model.getKeyid()));
        vipLevelMapper.deleteById(id);
    }
}
