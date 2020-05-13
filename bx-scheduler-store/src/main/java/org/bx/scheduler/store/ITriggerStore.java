package org.bx.scheduler.store;

import org.bx.scheduler.store.entity.SchedulerTriggerInfo;

import java.util.List;

public interface ITriggerStore {
    /**
     * 获取符合条件的触发器列表
     *
     * @param deptId
     * @param startTime
     * @param endTime
     * @param limit
     * @return
     */
    List<SchedulerTriggerInfo> getTriggerInfoList(String deptId, long startTime, long endTime, int limit);

    /**
     * 根据ID获取触发器详情
     *
     * @param triggerId
     * @return
     */
    SchedulerTriggerInfo getSchedulerTriggerInfoById(String triggerId);

    /**
     * 更新触发器信息
     *
     * @param triggerInfo
     */
    void updateSchedulerTriggerInfo(SchedulerTriggerInfo triggerInfo);

}
