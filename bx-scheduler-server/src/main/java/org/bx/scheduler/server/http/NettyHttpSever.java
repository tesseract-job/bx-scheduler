package org.bx.scheduler.server.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import lombok.extern.slf4j.Slf4j;
import org.bx.scheduler.common.lifecycle.AbstractLifecycle;
import org.bx.scheduler.engine.entity.SchedulerConfiguration;
import org.bx.scheduler.server.IEngineServer;

@Slf4j
public class NettyHttpSever extends AbstractLifecycle implements IEngineServer {
    private ChannelFuture channelFuture;
    private SchedulerConfiguration configuration;

    public NettyHttpSever(SchedulerConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void start() {
        super.start();
        final int engineServerPort = configuration.getEngineServerPort();
        log.info("NettyHttpEngineSever 服务启动,端口：{}", engineServerPort);
        ServerBootstrap b = new ServerBootstrap();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch)
                            throws Exception {
                        System.out.println("initChannel ch:" + ch);
                        ch.pipeline()
                                .addLast("decoder", new HttpRequestDecoder())
                                .addLast("encoder", new HttpResponseEncoder())
                                .addLast("aggregator", new HttpObjectAggregator(512 * 1024))
                                .addLast("nettyCommandDispatcher", new HttpDispatcher(configuration));
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);
        try {
            channelFuture = b.bind(engineServerPort).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            throw new RuntimeException("初始化NettyHttpEngineSever异常");
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            log.info("NettyHttpEngineSever stop");
        }
    }

    @Override
    public void stop() {
        super.stop();
        if (channelFuture != null) {
            channelFuture.channel().close();
        }
    }
}
