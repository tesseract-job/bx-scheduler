package org.bx.scheculer.scheduler.entity;

import lombok.Data;
import org.bx.scheduler.lock.entity.SchedulerLogInfo;
import org.bx.scheduler.store.entity.SchedulerTriggerInfo;

@Data
public class SchedulerContext {
    private SchedulerInfo schedulerInfo;
    private SchedulerLogInfo logInfo;
    private SchedulerTriggerInfo triggerInfo;
}
