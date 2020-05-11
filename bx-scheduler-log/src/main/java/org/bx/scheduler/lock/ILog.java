package org.bx.scheduler.lock;

import org.bx.scheduler.lock.entity.SchedulerLogInfo;

public interface ILog {
    void addLog(SchedulerLogInfo logInfo);


    void updateLog(SchedulerLogInfo logInfo);


    SchedulerLogInfo getLogById(String logId);
}
