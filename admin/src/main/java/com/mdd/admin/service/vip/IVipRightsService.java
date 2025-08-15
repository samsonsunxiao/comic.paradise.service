package com.mdd.admin.service.vip;

import java.util.List;

import com.mdd.admin.validate.vip.RightsSaveValidate;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.vip.VipRights;
/**
 * Vip服务接口类
 */
public interface IVipRightsService {

    /**
     * Vip 权益列表
     *
     * @return List<VipRights>
     */
    List<VipRights> all();

    /**
     * Vip 权益列表
     *
     * @author 
     */
    PageResult<VipRights> list();
    /**
     * Vip 权益详情
     *
     * @param key 
     * @return VipRights
     */
    VipRights detail(String key);
    /**
     * 保存
     *
     * @param rightsSaveValidate 
     */
    void save(RightsSaveValidate rightsSaveValidate);

    void del(Integer id);
}

