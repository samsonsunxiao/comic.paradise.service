package com.mdd.front.service;

import java.util.List;
import java.util.Map;

import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.validate.xmod.XModGameSearchValidate;
import com.mdd.front.vo.xmod.GameDetailVo;
import com.mdd.front.vo.xmod.GameSummaryVo;
import com.mdd.common.core.PageResult;

/**
 * 页面配置服务接口类
 * @author Admin
 */
public interface IGameService {

    Map<String, Map<String, Object>> list(PageValidate pageValidate, XModGameSearchValidate searchValidate);
    
    PageResult<GameSummaryVo> listStore(PageValidate pageValidate, XModGameSearchValidate searchValidate);
    
    GameDetailVo detail(String gid);
}
