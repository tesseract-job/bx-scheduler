package org.bx.scheduler.store;

import org.bx.scheduler.store.entity.SchedulerJobInfo;

public interface IJobInfoStore {
    SchedulerJobInfo getJobInfo(String triggerId);
}
