package com.mdd.admin.service;

import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.game.GameSaveValidate;
import com.mdd.admin.validate.game.GameSearchValidate;
import com.mdd.admin.vo.game.GameDetailVo;
import com.mdd.admin.vo.game.GameListVo;
import com.mdd.admin.vo.game.GameNameVo;
import com.mdd.common.core.PageResult;

import java.util.*;
/**
 * 源MOD服务接口类
 * 只提供查看，不提供修改和删除
 */
public interface IGameService {


    /**
    * 所有游戏列表
    */
    List<GameNameVo>  all(String status);
    
    /**
     * 游戏列表
     *
     * @param pageValidate   分页参数
     * @param searchValidate 搜索参数
     * @return PageResult<ArticleListVo>
     * @author fzr
     */
    PageResult<GameListVo> list(PageValidate pageValidate, GameSearchValidate searchValidate);

    /**
     * 资源详情
     *
     * @param id 主键ID
     * @author fzr
     */
    GameDetailVo detail(String gid);
    
     /**
     * 游戏编辑保存
     *
     * @param updateValidate 参数
     * @author fzr
     */
    void save(GameSaveValidate saveValidate);

    boolean checkKeyUriExisted(String keyuri);
}
