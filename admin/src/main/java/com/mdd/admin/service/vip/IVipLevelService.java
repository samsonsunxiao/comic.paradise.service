package com.mdd.admin.service.vip;

import com.mdd.admin.validate.vip.LevelSaveValidate;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.vip.VipLevel;
import com.mdd.common.entity.vip.VipLevelDetailVo;

import java.util.List;
/**
 * Vip服务接口类
 */
public interface IVipLevelService {

    /**
     * Vip 等级列表
     *
     * @author 
     */
    List<VipLevel> all();

     /**
     * Vip 支付模式列表
     *
     * @author 
     */
    PageResult<VipLevel> list();
    /**
     * Vip 等级详情
     *
     * @param levelid 
     * @return VipLevelDetailVo
     */
    VipLevelDetailVo detail(String keyid);
    /**
     * 保存
     *
     * @param levelSaveValidate 
     */
    void save(LevelSaveValidate levelSaveValidate);

    void del(Integer id);
}

