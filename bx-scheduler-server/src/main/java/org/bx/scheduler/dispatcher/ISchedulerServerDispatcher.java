package org.bx.scheduler.dispatcher;

import org.bx.scheduler.executor.entity.SchedulerExecutorContext;

public interface ISchedulerServerDispatcher {
    void dispatch(SchedulerExecutorContext executorContext);
}
