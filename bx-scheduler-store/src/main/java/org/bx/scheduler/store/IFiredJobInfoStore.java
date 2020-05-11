package org.bx.scheduler.store;

import org.bx.scheduler.store.entity.SchedulerFiredJobInfo;

public interface IFiredJobInfoStore {
    void addFiredJobInfo(SchedulerFiredJobInfo firedJobInfo);
}
