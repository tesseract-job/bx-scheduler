package org.bx.scheduler.log;

import org.bx.scheduler.log.entity.SchedulerLogInfo;

public interface ILog {
    void addLog(SchedulerLogInfo logInfo);


    void updateLog(SchedulerLogInfo logInfo);


    SchedulerLogInfo getLogById(String logId);
}
