package org.bx.scheduler.scheduler;

import org.bx.scheduler.scheduler.entity.SchedulerInfo;
import org.bx.scheduler.common.lifecycle.ILifecycle;

public interface IScheduler extends ILifecycle {
    void schedule(SchedulerInfo schedulerInfo) throws Exception;
}
