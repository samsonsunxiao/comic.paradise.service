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
import com.mdd.admin.service.vip.IVipPayService;
import com.mdd.admin.validate.vip.PaySaveValidate;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.vip.LevelPay;
import com.mdd.common.entity.vip.PayModel;
import com.mdd.common.mapper.vip.LevelPayMapper;
import com.mdd.common.mapper.vip.VipPayMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VipPayService implements IVipPayService{
    @Resource
    @Autowired
    VipPayMapper vipPayMapper;

    @Resource
    @Autowired
    LevelPayMapper levelPayMapper;

    public List<PayModel> all() {
        List<PayModel> listPay  = vipPayMapper.selectList(new QueryWrapper<PayModel>());
        return listPay;
    }
     /**
     * Vip 权益列表
     *
     * @author 
     */
    public PageResult<PayModel> list(){
         MPJQueryWrapper<PayModel> mpjQueryWrapper = new MPJQueryWrapper<PayModel>()
            .selectAll(PayModel.class);
        IPage<PayModel> iPage = vipPayMapper.selectJoinPage(
                new Page<>(0, -1),
                PayModel.class,
                mpjQueryWrapper);
        return PageResult.iPageHandle(iPage);
    }

    public PayModel detail(String key) {
        PayModel model = vipPayMapper.selectOne(
            new QueryWrapper<PayModel>().eq("keyid", key)
        );
        return model;
    }

    public void save(PaySaveValidate paySaveValidate)
    {
        PayModel model = vipPayMapper.selectOne(
            new QueryWrapper<PayModel>().eq("keyid", paySaveValidate.getKeyid())
        );
        Boolean isNew = false;
        if (model == null){
            model = new PayModel();
            isNew = true;
            long number =  100 + vipPayMapper.selectCount(new QueryWrapper<PayModel>()) + 1;
            StringBuilder sb = new StringBuilder();
            sb.append("P");
            sb.append(String.format("%02d", number % 100));
            model.setKeyid(sb.toString());
        }
        model.setTitle(paySaveValidate.getTitle());
        model.setPrice(paySaveValidate.getPrice());
        model.setImage(paySaveValidate.getImage());
        model.setRemark(paySaveValidate.getRemark());
        if (isNew){
            vipPayMapper.insert(model);
        }
        else{
            vipPayMapper.updateById(model);
        }
    }

    public void del(Integer id) {
        PayModel model = vipPayMapper.selectOne(
                new QueryWrapper<PayModel>()
                        .eq("id", id));
        Assert.notNull(model, "支付模式不存在");
        //先删除有关系的的等级
        levelPayMapper.delete(new QueryWrapper<LevelPay>().eq("paymodel", model.getKeyid()));
        vipPayMapper.deleteById(id);
    }
}
