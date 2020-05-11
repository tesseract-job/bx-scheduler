package org.bx.scheduler.executor.stratege;

import org.bx.scheculer.scheduler.entity.SchedulerContext;
import org.bx.scheduler.executor.entity.SchedulerExecutorContext;
import org.bx.scheduler.store.entity.SchedulerExecutorDetailIInfo;
import org.bx.scheduler.store.entity.SchedulerJobInfo;
import org.bx.scheduler.store.entity.SchedulerTriggerInfo;

import java.util.List;

public interface ISchedulerServerExecutorStratege {
    /**
     * 构建执行上下文信息
     *
     * @param schedulerContext
     * @param triggerInfo
     * @param executorDetailIInfoList
     * @param jobInfo
     * @return
     */
    List<SchedulerExecutorContext> buildSchedulerExecutorContext(SchedulerContext schedulerContext, SchedulerTriggerInfo triggerInfo,
                                                                 List<SchedulerExecutorDetailIInfo> executorDetailIInfoList,
                                                                 SchedulerJobInfo jobInfo);

    /**
     * 根据不同算法，从给定机器中选取执行机，用于重试
     *
     * @param executorDetailIInfoList
     * @return
     */
    SchedulerExecutorDetailIInfo selectExecutorDetail(List<SchedulerExecutorDetailIInfo> executorDetailIInfoList);
}
