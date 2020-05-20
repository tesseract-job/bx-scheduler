package org.bx.scheduler.client.executor;

import lombok.extern.slf4j.Slf4j;
import org.bx.scheduler.dispatcher.entity.ClientConfiguration;
import org.bx.scheduler.common.bean.TaskExecuteInfo;
import org.bx.scheduler.common.lifecycle.AbstractLifecycle;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DefaultTaskExecutor extends AbstractLifecycle implements ITaskExecutor {
    private ExecutorService executorService;
    private ClientConfiguration clientConfiguration;

    public DefaultTaskExecutor(ClientConfiguration clientConfiguration) {
        this.clientConfiguration = clientConfiguration;
    }

    @Override
    public void init() {
        executorService = new ThreadPoolExecutor(clientConfiguration.getCoreExecutorPoolSize(),
                clientConfiguration.getMaxExecutorPoolSize(), 60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(clientConfiguration.getBlockQueueSize()), r -> new Thread(r, "task-executor-thread"));
    }

    @Override
    public void execute(TaskExecuteInfo executeInfo) {

    }
}
