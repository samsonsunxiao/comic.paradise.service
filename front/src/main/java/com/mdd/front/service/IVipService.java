package com.mdd.front.service;

import java.util.List;

import com.mdd.common.entity.vip.VipLevelDetailVo;
import com.mdd.front.validate.VipPayVaildate;

public interface IVipService {

    List<VipLevelDetailVo> listVip();

    Object getPayQr(VipPayVaildate VipPayVaildate,Integer userId,Integer terminal);
}
