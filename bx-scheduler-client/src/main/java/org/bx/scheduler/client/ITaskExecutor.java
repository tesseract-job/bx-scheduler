package org.bx.scheduler.client;

import org.bx.scheduler.common.bean.TaskExecuteInfo;

public interface ITaskExecutor {
    void execute(TaskExecuteInfo executeInfo);
}
