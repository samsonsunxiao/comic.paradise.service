package com.mdd.front.service;

import java.util.List;
import java.util.Map;

import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.vo.xmod.XModSuggestVo;
import com.mdd.common.core.PageResult;

/**
 * 页面配置服务接口类
 * @author Admin
 */
public interface IXModSearch {

    List<XModSuggestVo> suggest(String keyword);

}
