package org.bx.scheduler.engine;

import org.bx.scheculer.scheduler.DefaultScheduler;
import org.bx.scheduler.dispatcher.DefaultDispatcher;
import org.bx.scheduler.engine.entity.SchedulerConfiguration;
import org.bx.scheduler.executor.DefaultExecutor;
import org.bx.scheduler.executor.threadPool.DefaultSchedulerThreadPool;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DefaultEngine implements IEngine {
    private SchedulerConfiguration configuration;


    @Override
    public void init() {
        final ThreadPoolExecutor retryExecutor = new ThreadPoolExecutor(10,
                10, 0, TimeUnit.MILLISECONDS, new SynchronousQueue<>());
        final DefaultSchedulerThreadPool defaultSchedulerThreadPool = new DefaultSchedulerThreadPool();
        final DefaultScheduler defaultScheduler = new DefaultScheduler();
        final DefaultExecutor executor = new DefaultExecutor(defaultSchedulerThreadPool, retryExecutor);
        final DefaultDispatcher defaultDispatcher = new DefaultDispatcher();
        configuration.setDispatcher(defaultDispatcher);
        configuration.setExecutor(executor);
        configuration.setThreadPool(defaultSchedulerThreadPool);
        configuration.setScheduler(defaultScheduler);
    }

    @Override
    public void start() {
        
    }

    @Override
    public void stop() {

    }
}
