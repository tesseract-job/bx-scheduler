package org.bx.scheduler.scheduler.entity;

import lombok.Data;
import org.bx.scheduler.entity.SchedulerConfiguration;
import org.bx.scheduler.store.entity.SchedulerDeptInfo;

@Data
public class SchedulerInfo {
    private SchedulerDeptInfo deptInfo;
    private SchedulerConfiguration configuration;
}
