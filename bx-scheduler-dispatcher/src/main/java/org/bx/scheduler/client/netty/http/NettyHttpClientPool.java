package org.bx.scheduler.client.netty.http;

import org.bx.scheduler.client.IClientPool;
import org.bx.scheduler.client.IDispatcherClient;
import org.bx.scheduler.common.lifecycle.AbstractLifecycle;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NettyHttpClientPool extends AbstractLifecycle implements IClientPool {
    private final Map<String, IDispatcherClient> clientMap = new ConcurrentHashMap<>(256);

    /**
     * 允许多线程操作，极端情况产生多个httpclient 但是在很短暂时间内能够完成
     *
     * @param socket
     * @return
     */
    @Override
    public IDispatcherClient getClient(String socket) {
        IDispatcherClient client = clientMap.get(socket);
        if (client == null) {
            client = new NettyHttpClient(socket);
            clientMap.put(socket, client);
        }
        return client;
    }

}
