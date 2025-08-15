package com.mdd.front.service;

import com.mdd.common.core.PageResult;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.validate.users.*;
import com.mdd.front.vo.user.UserAccountListVo;
import com.mdd.front.vo.user.UserCenterVo;
import com.mdd.front.vo.user.UserInfoVo;

/**
 * 用户服务接口类
 */
public interface IUserAccountLogService {

    /**
     * @notes 获取列表
     * @return array
     * @author damonyuan
     */
    PageResult<UserAccountListVo> lists(PageValidate pageValidate, UserAccountLogSearchValidate searchValidate);
}
