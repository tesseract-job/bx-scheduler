package org.bx.scheduler.client;

import org.bx.scheduler.executor.entity.SchedulerExecutorContext;

public interface ISchedulerServerDispatcher {
    void dispatch(SchedulerExecutorContext executorContext);
}
