package org.bx.scheduler.dispatcher.netty.http;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.bx.scheduler.common.bean.InfoWrapper;
import org.bx.scheduler.common.bean.TaskExecuteInfo;
import org.bx.scheduler.common.bean.TaskExecuteRespInfo;
import org.bx.scheduler.common.lifecycle.AbstractLifecycle;
import org.bx.scheduler.common.util.HttpUtils;
import org.bx.scheduler.dispatcher.IDispatcherClient;
import org.bx.scheduler.dispatcher.entity.DispatchContext;
import org.bx.scheduler.entity.SchedulerConfiguration;

import java.net.URI;
import java.util.concurrent.atomic.AtomicLong;

import static org.bx.scheduler.common.constant.CommonConstant.FAILED_STATUS;
import static org.bx.scheduler.common.constant.CommonConstant.TASK_EXECUTE_HANDLE;

@Slf4j
public class NettyHttpClient extends AbstractLifecycle implements IDispatcherClient {
    private Channel channel;
    private CountDownLatchStore countDownLatchStore = new CountDownLatchStore();
    private AtomicLong futureIdAtomic = new AtomicLong();
    private String socket;

    public NettyHttpClient(String socket, Channel channel) {
        this.channel = channel;
        this.socket = socket;
    }

    @Override
    public void request(DispatchContext dispatchContext) throws Exception {
        final TaskExecuteInfo executeInfo = dispatchContext.getExecuteInfo();
        final String futureId = futureIdAtomic.getAndIncrement() + "";
        executeInfo.setFutureId(futureId);
        final InfoWrapper infoWrapper = new InfoWrapper(executeInfo, TASK_EXECUTE_HANDLE);
        log.info("start dispatch task:{}", infoWrapper);
        final SchedulerConfiguration configuration = dispatchContext.getConfiguration();
        final URI uri = URI.create(socket);
        final FullHttpRequest fullHttpRequest = HttpUtils.buildDefaultFullHttpRequest(uri.getHost(), "/",
                configuration.getSerializer().serialize(infoWrapper));
        final InfoWrapper resultIW = this.countDownLatchStore.
                run(() -> channel.writeAndFlush(fullHttpRequest), futureId);
        final TaskExecuteRespInfo taskExecuteRespInfo = (TaskExecuteRespInfo) resultIW.getInfo();
        if (taskExecuteRespInfo.getState() == FAILED_STATUS) {
            throw new RuntimeException(taskExecuteRespInfo.getException());
        }
        log.info("dispatch success response: {}", taskExecuteRespInfo);
    }


    @Override
    public void stop() {
        if (channel.isActive()) {
            channel.close();
        }
    }
}
