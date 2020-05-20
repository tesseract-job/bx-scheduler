package org.bx.scheduler.dispatcher.netty.http;

import lombok.extern.slf4j.Slf4j;
import org.bx.scheduler.dispatcher.IClientPool;
import org.bx.scheduler.dispatcher.IDispatcherClient;
import org.bx.scheduler.common.lifecycle.AbstractLifecycle;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class NettyHttpClientPool extends AbstractLifecycle implements IClientPool {
    private final Map<String, IDispatcherClient> clientMap = new ConcurrentHashMap<>(256);

    @Override
    public IDispatcherClient getClient(String socket) {
        IDispatcherClient client = clientMap.get(socket);
        if (client == null) {
            throw new RuntimeException("找不到对应客户端:" + socket);
        }
        return client;
    }

    @Override
    public void storeClient(String socket, IDispatcherClient dispatcherClient) throws Exception {
        IDispatcherClient client = clientMap.get(socket);
        if (client == null) {
            clientMap.put(socket, dispatcherClient);
            return;
        }
        log.error("重复注册");
    }

    @Override
    public void removeClient(String socket) throws Exception {
        clientMap.remove(socket);
    }

}
