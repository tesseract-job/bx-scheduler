package org.bx.scheduler.client.entity;

import lombok.Data;
import org.bx.scheduler.common.bean.TaskExecuteInfo;
import org.bx.scheduler.engine.entity.SchedulerConfiguration;

@Data
public class DispatchContext {
    private TaskExecuteInfo executeInfo;
    private SchedulerConfiguration configuration;
}
