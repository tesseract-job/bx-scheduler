package org.bx.scheduler.dispatcher;

import org.bx.scheduler.common.lifecycle.ILifecycle;

public interface IClientPool extends ILifecycle {
    IDispatcherClient getClient(String socket);

    void storeClient(String socket, IDispatcherClient client);

    void removeClient(String socket);
}
