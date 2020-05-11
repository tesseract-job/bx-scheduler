package org.bx.scheduler.client;

import org.bx.scheduler.common.bean.TaskExecuteInfo;


public interface IDispatcherClient {
    void request(TaskExecuteInfo taskRequestInfo);
}
