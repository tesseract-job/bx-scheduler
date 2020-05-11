package org.bx.scheculer.scheduler;

import org.bx.scheculer.scheduler.entity.SchedulerInfo;
import org.bx.scheduler.common.lifecycle.ILifecycle;

public interface IScheduler extends ILifecycle {
    void schedule(SchedulerInfo schedulerInfo) throws Exception;
}
