package com.mdd.front.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mdd.common.aop.NotLogin;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.entity.notice.NoticeRecord;
import com.mdd.common.entity.smsLog.SmsLog;
import com.mdd.common.enums.NoticeEnum;
import com.mdd.common.enums.SmsEnum;
import com.mdd.common.enums.YesNoEnum;
import com.mdd.common.exception.OperateException;
import com.mdd.common.mapper.notice.NoticeRecordMapper;
import com.mdd.common.mapper.smsLog.SmsLogMapper;
import com.mdd.common.plugin.notice.NoticeDriver;
import com.mdd.common.plugin.notice.vo.NoticeSmsVo;
import com.mdd.common.util.StringUtils;
import com.mdd.common.util.ToolUtils;
import com.mdd.common.validator.annotation.IDMust;
import com.mdd.front.service.IIndexService;
import com.mdd.front.validate.common.SmsValidate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sms")
@Api(tags = "主页管理")
public class SmsController {

    @Resource
    SmsLogMapper smsLogMapper;

    @NotLogin
    @PostMapping("/sendCode")
    @ApiOperation(value="发送短信")
    public AjaxResult<Object> sendSms(@Validated @RequestBody SmsValidate smsValidate) {
        QueryWrapper smsLogQueryWrapper = new QueryWrapper<SmsLog>();
        smsLogQueryWrapper.eq("mobile", smsValidate.getMobile());
        smsLogQueryWrapper.eq("send_status", SmsEnum.SEND_SUCCESS.getCode());
        smsLogQueryWrapper.in("scene_id", NoticeEnum.getSmsScene());
        smsLogQueryWrapper.eq("is_verify", YesNoEnum.NO.getCode());
        if (StringUtils.isNotNull(smsValidate.getScene())) {
            smsLogQueryWrapper.eq("scene_id", NoticeEnum.getSceneByTag(smsValidate.getScene()));
        }
        smsLogQueryWrapper.orderByDesc("send_time");
        smsLogQueryWrapper.last("limit 1");
        SmsLog smsLog = smsLogMapper.selectOne(smsLogQueryWrapper);
        if (StringUtils.isNotNull(smsLog)) {
            if (smsLog.getSendTime() + 5 * 60 > System.currentTimeMillis() / 1000 ) {
                throw new OperateException("已有短信记录，请勿重复发送");
            }
        }

        String code = ToolUtils.randomInt(4);
        NoticeSmsVo params = new NoticeSmsVo()
                .setScene(NoticeEnum.getSceneByTag(smsValidate.getScene()))
                .setMobile(smsValidate.getMobile())
                .setExpire(900)
                .setParams(new String[] {
                        "code:" + code
                })
                .setCode(code);

        NoticeDriver.handle(params);
        return AjaxResult.success();
    }

}
