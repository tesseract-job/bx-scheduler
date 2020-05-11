package org.bx.scheduler.common.util;

import org.bx.scheculer.scheduler.entity.SchedulerContext;
import org.bx.scheculer.scheduler.entity.SchedulerInfo;
import org.bx.scheduler.engine.entity.SchedulerConfiguration;
import org.bx.scheduler.lock.ILog;
import org.bx.scheduler.lock.entity.SchedulerLogInfo;
import org.bx.scheduler.store.entity.SchedulerDeptInfo;
import org.bx.scheduler.store.entity.SchedulerTriggerInfo;

public class CommonUtis {
    public static SchedulerContext createSchedulerContext(SchedulerConfiguration configuration, SchedulerInfo schedulerInfo, SchedulerTriggerInfo triggerInfo) {
        final SchedulerContext schedulerContext = new SchedulerContext();
        final SchedulerLogInfo schedulerLogInfo = SchedulerLogInfo.createDefaultSchedulerLogInfo(configuration.getIdGenerator());
        final ILog logger = configuration.getLogger();
        final SchedulerDeptInfo deptInfo = schedulerInfo.getDeptInfo();
        schedulerLogInfo.setTriggerName(triggerInfo.getName());
        schedulerLogInfo.setDeptId(deptInfo.getId());
        schedulerLogInfo.setDeptName(deptInfo.getName());
        schedulerLogInfo.setStrategy(triggerInfo.getStrategy());
        logger.addLog(schedulerLogInfo);
        schedulerContext.setLogInfo(schedulerLogInfo);
        schedulerContext.setTriggerInfo(triggerInfo);
        schedulerContext.setSchedulerInfo(schedulerInfo);
        return schedulerContext;
    }
}
