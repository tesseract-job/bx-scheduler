package org.bx.scheduler.store;

import org.bx.scheduler.store.entity.SchedulerTriggerInfo;

import java.util.List;

public interface ITriggerStore {

    List<SchedulerTriggerInfo> getTriggerInfoList(String deptId, long startTime, long endTime, int limit);

    SchedulerTriggerInfo getSchedulerTriggerInfoById(String triggerId);

}
