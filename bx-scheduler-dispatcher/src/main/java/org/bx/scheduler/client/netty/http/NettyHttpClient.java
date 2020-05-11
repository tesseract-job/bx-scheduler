package org.bx.scheduler.client.netty.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import lombok.extern.slf4j.Slf4j;
import org.bx.scheduler.client.IDispatcherClient;
import org.bx.scheduler.client.entity.DispatchContext;
import org.bx.scheduler.common.bean.TaskExecuteInfo;
import org.bx.scheduler.common.lifecycle.AbstractLifecycle;
import org.bx.scheduler.common.util.HttpUtils;
import org.bx.scheduler.engine.entity.SchedulerConfiguration;

import java.net.URI;

@Slf4j
public class NettyHttpClient extends AbstractLifecycle implements IDispatcherClient {
    private String socket;
    private EventLoopGroup eventLoopGroup;
    private Channel channel;
    private URI targetURI;

    public NettyHttpClient(String socket) {
        this.socket = socket;
        init();
    }

    @Override
    public void request(DispatchContext dispatchContext) throws Exception {
        final TaskExecuteInfo executeInfo = dispatchContext.getExecuteInfo();
        log.info("start dispatch task:{}", executeInfo);
        final SchedulerConfiguration configuration = dispatchContext.getConfiguration();
        final FullHttpRequest fullHttpRequest = HttpUtils.buildDefaultFullHttpRequest(targetURI,
                configuration.getSerializer().serialize(executeInfo));
        getActiveChannel().writeAndFlush(fullHttpRequest);
    }


    @Override
    public void init() {
        try {
            targetURI = URI.create(socket);
            connect();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        if (channel.isActive()) {
            channel.close();
        }
    }

    private Channel getActiveChannel() {
        if (channel == null || !channel.isActive()) {
            try {
                this.connect();
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new RuntimeException("初始化channel出错");
            }
        }
        return this.channel;
    }

    private void connect() throws Exception {
        eventLoopGroup = new NioEventLoopGroup(1);
        Bootstrap b = new Bootstrap();
        b.group(eventLoopGroup);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new HttpResponseDecoder());
                ch.pipeline().addLast(new HttpRequestEncoder());
                ch.pipeline().addLast(new HttpObjectAggregator(5 * 1024));
            }
        });

        ChannelFuture f = b.connect(targetURI.getHost(), targetURI.getPort()).sync();
        this.channel = f.channel();
    }
}
