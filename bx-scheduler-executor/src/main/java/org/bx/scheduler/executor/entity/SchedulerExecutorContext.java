package org.bx.scheduler.executor.entity;

import lombok.Data;
import org.bx.scheculer.scheduler.entity.SchedulerContext;
import org.bx.scheduler.lock.entity.SchedulerLogInfo;
import org.bx.scheduler.store.entity.SchedulerExecutorDetailIInfo;
import org.bx.scheduler.store.entity.SchedulerJobInfo;

@Data
public class SchedulerExecutorContext {
    private SchedulerContext schedulerContext;
    private SchedulerExecutorDetailIInfo executorDetailIInfo;
    private SchedulerJobInfo jobInfo;
    private SchedulerLogInfo contextLogInfo;
    private int shardingIndex;
    private int retryCount;
}
