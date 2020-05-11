package org.bx.scheduler.dispatcher;

import org.bx.scheduler.executor.entity.SchedulerExecutorContext;

public interface IDispatcher {
    void dispatch(SchedulerExecutorContext executorContext);
}
