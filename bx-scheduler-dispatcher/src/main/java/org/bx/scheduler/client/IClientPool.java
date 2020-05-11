package org.bx.scheduler.client;

import org.bx.scheduler.common.lifecycle.ILifecycle;

public interface IClientPool extends ILifecycle {
    IDispatcherClient getClient(String socket) throws Exception;
}
