package com.mdd.front.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.log.UserAccountLog;
import com.mdd.common.enums.LogMoneyEnum;
import com.mdd.common.mapper.log.UserAccountLogMapper;
import com.mdd.common.util.TimeUtils;
import com.mdd.front.service.ILogsService;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.vo.RechargeRecordVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

@Service
public class LogsServiceImpl implements ILogsService {

    @Resource
    UserAccountLogMapper userAccountLogMapper;

    @Override
    public PageResult<RechargeRecordVo> userMoney(PageValidate pageValidate, Integer userId, Integer type) {
        Integer pageNo   = pageValidate.getPage_no();
        Integer pageSize = pageValidate.getPage_size();

        QueryWrapper<UserAccountLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("id");
        if (type > 0) {
            queryWrapper.eq("action", type);
        }

        IPage<UserAccountLog> iPage = userAccountLogMapper.selectPage(new Page<>(pageNo, pageSize), queryWrapper);

        List<RechargeRecordVo> list = new LinkedList<>();
        for (UserAccountLog logMoney : iPage.getRecords()) {
            RechargeRecordVo vo = new RechargeRecordVo();

            vo.setId(logMoney.getId());
            vo.setAction(logMoney.getAction());
            vo.setOrderAmount(logMoney.getChangeAmount());
            vo.setTips(LogMoneyEnum.getMsgByCode(logMoney.getChangeType()));
            vo.setCreateTime(TimeUtils.timestampToDate(logMoney.getCreateTime()));
            list.add(vo);
        }

        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), list);
    }

}
