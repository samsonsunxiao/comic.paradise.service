package com.mdd.front.service;
import com.mdd.common.core.PageResult;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.vo.xmod.ModSummaryVo;
import com.mdd.front.vo.xmod.ModDetailVo;
import com.mdd.front.vo.xmod.ModDownloadVo;
/**
 * 页面配置服务接口类
 * @author 
 */
public interface IModService {

    ModDetailVo detail(String modid);

    PageResult<ModSummaryVo> listStore(PageValidate pageValidate, String searchParam);

    ModDownloadVo queryDownload(String modid, String supply, String version, Integer terminal, Integer userId);
}
