package org.bx.scheduler.store;

import org.bx.scheduler.store.entity.SchedulerExecutorInfo;

public interface IExecutorStore {
    SchedulerExecutorInfo getExecutorInfo(String executorId);
}
