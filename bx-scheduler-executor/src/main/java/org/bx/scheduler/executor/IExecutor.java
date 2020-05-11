package org.bx.scheduler.executor;

import org.bx.scheculer.scheduler.entity.SchedulerContext;
import org.bx.scheculer.scheduler.entity.SchedulerInfo;
import org.bx.scheduler.common.bean.TaskExecuteInfo;
import org.bx.scheduler.executor.entity.SchedulerExecutorContext;

public interface IExecutor {
    /**
     * 调度数量
     *
     * @return
     */
    int scheduleNum();

    /**
     * 执行任务
     *
     * @param schedulerContext
     */
    void execute(SchedulerContext schedulerContext);

    /**
     * 在执行上下文的重试
     *
     * @param schedulerExecutorContext
     */
    void taskRetry(SchedulerExecutorContext schedulerExecutorContext);

    /**
     * 在任务完成时的重试
     *
     * @param executeInfo
     * @param schedulerInfo
     */
    void taskRetry(TaskExecuteInfo executeInfo, SchedulerInfo schedulerInfo);
}
