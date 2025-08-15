package com.mdd.admin.service.vip;

import com.mdd.admin.validate.vip.PaySaveValidate;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.vip.PayModel;

import java.util.List;
/**
 * Vip服务接口类
 */
public interface IVipPayService {
     /**
     * Vip 支付模式列表
     *
     * @return List<PayModel>
     */
    List<PayModel> all();

    /**
     * Vip 支付模式列表
     *
     * @author 
     */
    PageResult<PayModel> list();
    /**
     * Vip 支付模式详情
     *
     * @param key 
     * @return VipRights
     */
    PayModel detail(String key);
    /**
     * 保存
     *
     * @param paySaveValidate 
     */
    void save(PaySaveValidate paySaveValidate);

    void del(Integer id);
}

