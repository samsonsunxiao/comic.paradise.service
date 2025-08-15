package com.mdd.front.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mdd.common.config.GlobalConfig;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.article.Article;
import com.mdd.common.entity.log.UserAccountLog;
import com.mdd.common.entity.user.User;
import com.mdd.common.entity.user.UserAuth;
import com.mdd.common.enums.AccountLogEnum;
import com.mdd.common.enums.ClientEnum;
import com.mdd.common.enums.NoticeEnum;
import com.mdd.common.enums.UserEnum;
import com.mdd.common.exception.OperateException;
import com.mdd.common.mapper.log.UserAccountLogMapper;
import com.mdd.common.mapper.user.UserAuthMapper;
import com.mdd.common.mapper.user.UserMapper;
import com.mdd.common.plugin.notice.NoticeCheck;
import com.mdd.common.plugin.wechat.WxMnpDriver;
import com.mdd.common.util.*;
import com.mdd.front.FrontThreadLocal;
import com.mdd.front.service.IUserAccountLogService;
import com.mdd.front.service.IUserService;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.validate.users.*;
import com.mdd.front.vo.article.ArticleListedVo;
import com.mdd.front.vo.user.UserAccountListVo;
import com.mdd.front.vo.user.UserCenterVo;
import com.mdd.front.vo.user.UserInfoVo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 用户服务实现类
 */
@Service
public class UserAccountLogServiceImpl implements IUserAccountLogService {

    @Resource
    UserAccountLogMapper userAccountLogMapper;

    @Override
    public PageResult<UserAccountListVo> lists(PageValidate pageValidate, UserAccountLogSearchValidate searchValidate) {
        Integer pageNo   = pageValidate.getPage_no();
        Integer pageSize = pageValidate.getPage_size();

        QueryWrapper<UserAccountLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("delete_time");
        queryWrapper.eq("user_id", searchValidate.getUserId());
        if (StringUtils.isNotEmpty(searchValidate.getType()) && searchValidate.getType().equals("um")) {
            queryWrapper.in("change_type", AccountLogEnum.getUserMoneyChangeType());
        }

        if (StringUtils.isNotNull(searchValidate.getAction())) {
            queryWrapper.eq("action", searchValidate.getAction());
        }

        queryWrapper.orderByDesc("id");
        IPage<UserAccountLog> iPage = userAccountLogMapper.selectPage(new Page<>(pageNo, pageSize), queryWrapper);
        List<UserAccountListVo> list = new LinkedList<>();
        for (UserAccountLog item : iPage.getRecords()) {
            UserAccountListVo vo = new UserAccountListVo();
            BeanUtils.copyProperties(item, vo);
            vo.setTypeDesc(AccountLogEnum.getChangeTypeDesc(item.getChangeType()));
            String symbol = item.getAction().equals(AccountLogEnum.DEC.getCode()) ? "-" : "+";
            vo.setChangeAmountDesc(symbol + item.getChangeAmount());
            vo.setCreateTime(TimeUtils.timestampToDate(item.getCreateTime()));
            list.add(vo);
        }

        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), list);
    }
}
