package org.bx.scheduler.client;

import org.bx.scheduler.client.entity.DispatchContext;
import org.bx.scheduler.common.lifecycle.ILifecycle;


public interface IDispatcherClient extends ILifecycle {
    void request(DispatchContext dispatchContext) throws Exception;
}
