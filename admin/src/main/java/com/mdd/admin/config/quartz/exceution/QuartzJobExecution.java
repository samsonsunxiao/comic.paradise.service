package com.mdd.admin.config.quartz.exceution;

import com.mdd.admin.config.quartz.InvokeUtils;
import com.mdd.common.entity.DevCrontab;
import org.quartz.JobExecutionContext;

/**
 * 允许并发任务
 */
public class QuartzJobExecution extends AbstractQuartzJob {

    @Override
    protected void doExecute(JobExecutionContext context, DevCrontab crontab) throws Exception {
        InvokeUtils.invokeMethod(crontab);
    }

}
