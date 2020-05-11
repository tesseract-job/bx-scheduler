package org.bx.scheduler.store;

import org.bx.scheduler.store.entity.SchedulerExecutorDetailIInfo;

import java.util.List;

public interface IExecutorDetailStore {
    /**
     * @param executorId      执行器id
     * @param excludeDetailId 需要排除的执行机器id
     * @return
     */
    List<SchedulerExecutorDetailIInfo> getExecutorDetailInfo(String executorId, String... excludeDetailId);
}
