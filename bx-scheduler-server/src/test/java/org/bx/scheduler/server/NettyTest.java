package org.bx.scheduler.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.bx.scheduler.common.util.HttpUtils;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class NettyTest {
    static class ServerChannelInBound extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;
            final byte[] bytes = ByteBufUtil.getBytes(fullHttpRequest.content());
            final String content = new String(bytes);
            log.info("server 接受信息:{}", content);
            final FullHttpResponse ok = HttpUtils.buildFullHttpResponse(bytes, null);
            ctx.writeAndFlush(ok);
        }
    }

    static class ClientChannelInBound extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            log.info("client 接受信息:{}", msg);
        }
    }

    @Data
    static class NettyClient {
        Bootstrap bootstrap;

        public void start() {
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
                    ch.pipeline().addLast(new ClientChannelInBound());
                }
            });
        }

        public static void main(String[] args) throws Exception {
            final NettyClient nettyClient = new NettyClient();
            nettyClient.start();
            final ChannelFuture future = nettyClient.bootstrap.connect("localhost", 8080).sync();
            log.info("连接成功");
            final Channel channel = future.channel();
            final AtomicInteger atomicInteger = new AtomicInteger();
            for (int i = 0; i < 100; i++) {
                final byte[] bytes = String.valueOf(atomicInteger.getAndDecrement()).getBytes();
                final FullHttpRequest fullHttpRequest = HttpUtils.buildDefaultFullHttpRequest("localhost", "/", bytes);
                new Thread(() -> channel.writeAndFlush(fullHttpRequest)).start();
            }
        }
    }

    static class NettyServer {
        public void start() {
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
                                    .addLast("nettyCommandDispatcher", new ServerChannelInBound());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);
            try {
                ChannelFuture channelFuture = b.bind(8080).sync();
                log.info("server started");
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

        public static void main(String[] args) {
            final NettyServer nettyServer = new NettyServer();
            nettyServer.start();
        }

    }


}
