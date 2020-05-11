package org.bx.scheduler.executor.stratege;

import org.bx.scheduler.executor.entity.SchedulerExecutorContext;
import org.bx.scheduler.store.entity.SchedulerExecutorDetailIInfo;
import org.bx.scheduler.store.entity.SchedulerJobInfo;
import org.bx.scheduler.store.entity.SchedulerTriggerInfo;

import java.util.List;

public interface IExecutorStratege {
    /**
     * @param triggerInfo             触发器信息
     * @param executorDetailIInfoList 执行器列表
     * @param jobInfo                 任务详情
     * @return
     */
    List<SchedulerExecutorContext> buildSchedulerExecutorContext(SchedulerTriggerInfo triggerInfo,
                                                                 List<SchedulerExecutorDetailIInfo> executorDetailIInfoList,
                                                                 SchedulerJobInfo jobInfo);


    SchedulerExecutorDetailIInfo selectExecutorDetail(List<SchedulerExecutorDetailIInfo> executorDetailIInfoList);
}
