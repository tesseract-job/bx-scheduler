package org.bx.scheduler.client.buddy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bx.scheduler.client.entity.ClientConfiguration;
import org.bx.scheduler.common.bean.*;
import org.bx.scheduler.common.lifecycle.AbstractLifecycle;

import java.net.URI;
import java.util.List;

import static org.bx.scheduler.common.constant.CommonConstant.*;

@Slf4j
public class NettyHttpBuddy extends AbstractLifecycle implements IClientBuddy {
    private Channel serverChannel;
    private EventLoopGroup eventLoopGroup;
    private Bootstrap bootstrap;
    private ClientConfiguration clientConfiguration;

    public NettyHttpBuddy(ClientConfiguration clientConfiguration) {
        this.clientConfiguration = clientConfiguration;
    }

    @SneakyThrows
    @Override
    public void register(List<TaskInfo> taskInfoList) {
        if (taskInfoList == null || taskInfoList.isEmpty()) {
            throw new RuntimeException("registerInfoList is null");
        }
        final URI localURI = clientConfiguration.getLocalURI();
        final RegisterInfo registerInfo = new RegisterInfo(localURI.getHost(), localURI.getPort(), taskInfoList);
        final InfoWrapper infoWrapper = new InfoWrapper();
        infoWrapper.setHandler(HEARTBEAT_HANDLE);
        infoWrapper.setInfo(registerInfo);
        getActiveChannel().writeAndFlush(clientConfiguration.getSerializer().serialize(infoWrapper)).sync();
    }

    @SneakyThrows
    @Override
    public void heartbeat() {
        final URI localURI = clientConfiguration.getLocalURI();
        final ServerHeartbeatInfo registerInfo = new ServerHeartbeatInfo(localURI.getHost(), localURI.getPort());
        final InfoWrapper infoWrapper = new InfoWrapper();
        infoWrapper.setHandler(REGISTER_HANDLE);
        infoWrapper.setInfo(registerInfo);
        getActiveChannel().writeAndFlush(clientConfiguration.getSerializer().serialize(infoWrapper)).sync();
    }

    @SneakyThrows
    @Override
    public void taskNotify(TaskCompleteInfo completeInfo) {
        final URI localURI = clientConfiguration.getLocalURI();
        final InfoWrapper infoWrapper = new InfoWrapper();
        infoWrapper.setHandler(TASK_COMPLETE_HANDLE);
        infoWrapper.setInfo(completeInfo);
        getActiveChannel().writeAndFlush(clientConfiguration.getSerializer().serialize(infoWrapper)).sync();
    }

    @Override
    public void init() {
        super.init();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new HttpResponseDecoder());
                ch.pipeline().addLast(new HttpRequestEncoder());
                ch.pipeline().addLast(new HttpObjectAggregator(5 * 1024));
                ch.pipeline().addLast(new HttpDispatcher(clientConfiguration));
            }
        });
    }

    @Override
    public void start() {
        super.start();
        this.connect();
    }

    @Override
    public void stop() {
        super.stop();
        if (serverChannel != null) {
            serverChannel.close();
        }
        if (eventLoopGroup != null) {
            eventLoopGroup.shutdownGracefully();
        }
    }

    private Channel getActiveChannel() {
        if (!this.serverChannel.isActive()) {
            this.connect();
        }
        return this.serverChannel;
    }

    private void connect() {
        final URI serverURI = clientConfiguration.getServerURI();
        ChannelFuture f;
        try {
            log.info("start connect : {}", serverURI);
            f = bootstrap.connect(serverURI.getHost(), serverURI.getPort()).sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.serverChannel = f.channel();
        log.info(" connect success");
    }
}
