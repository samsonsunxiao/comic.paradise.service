package com.mdd.common.mapper.smsLog;

import com.mdd.common.core.basics.IBaseMapper;
import com.mdd.common.entity.Config;
import com.mdd.common.entity.smsLog.SmsLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 短信记录
 */
@Mapper
public interface SmsLogMapper extends IBaseMapper<SmsLog> {
}
