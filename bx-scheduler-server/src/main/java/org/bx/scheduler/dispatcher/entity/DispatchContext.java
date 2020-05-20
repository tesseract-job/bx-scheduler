package org.bx.scheduler.dispatcher.entity;

import lombok.Data;
import org.bx.scheduler.common.bean.TaskExecuteInfo;
import org.bx.scheduler.entity.SchedulerConfiguration;

@Data
public class DispatchContext {
    private TaskExecuteInfo executeInfo;
    private SchedulerConfiguration configuration;
}
