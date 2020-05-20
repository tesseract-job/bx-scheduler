package org.bx.scheduler.client.buddy;

import org.bx.scheduler.common.bean.TaskCompleteInfo;
import org.bx.scheduler.common.bean.TaskInfo;
import org.bx.scheduler.common.lifecycle.ILifecycle;

import java.util.List;

public interface IClientBuddy extends ILifecycle {
    /**
     * 注册
     *
     * @param taskInfoList
     */
    void register(List<TaskInfo> taskInfoList);

    /**
     * 心跳
     */
    void heartbeat();

    /**
     * 告知任务完成情况
     *
     * @param completeInfo
     */
    void taskNotify(TaskCompleteInfo completeInfo);
}
