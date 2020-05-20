package org.bx.scheduler.dispatcher;

import org.bx.scheduler.common.lifecycle.ILifecycle;

public interface IClientPool extends ILifecycle {
    IDispatcherClient getClient(String socket) throws Exception;

    void storeClient(String socket, IDispatcherClient client) throws Exception;

    void removeClient(String socket) throws Exception;
}
