package org.bx.scheduler.dispatcher;

import org.bx.scheduler.dispatcher.entity.DispatchContext;
import org.bx.scheduler.common.lifecycle.ILifecycle;


public interface IDispatcherClient extends ILifecycle {
    void request(DispatchContext dispatchContext) throws Exception;
}
