package org.bx.scheduler.server.http;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.bx.scheduler.common.bean.ClientHeartbeatRespInfo;
import org.bx.scheduler.common.bean.InfoWrapper;
import org.bx.scheduler.common.serializer.ISerializer;
import org.bx.scheduler.common.util.HttpUtils;
import org.bx.scheduler.entity.SchedulerConfiguration;
import org.bx.scheduler.server.HandleContext;
import org.bx.scheduler.server.handler.ClientHeartbeatHandler;
import org.bx.scheduler.server.handler.ClientRegisterHandler;
import org.bx.scheduler.server.handler.ITaskHandler;

import java.util.HashMap;
import java.util.Map;

import static org.bx.scheduler.common.constant.CommonConstant.*;

@Slf4j
public class HttpDispatcher extends ChannelInboundHandlerAdapter {
    private final Map<String, ITaskHandler> handlerMap = new HashMap<>(8);


    private SchedulerConfiguration configuration;


    public HttpDispatcher(SchedulerConfiguration configuration) {
        this.configuration = configuration;
        initHandlerMap();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;
        final ISerializer serializer = configuration.getSerializer();
        final InfoWrapper wrapper = serializer.deserialize(ByteBufUtil.getBytes(fullHttpRequest.content()), InfoWrapper.class);
        final ITaskHandler iTaskHandler = handlerMap.get(wrapper.getHandler());
        if (iTaskHandler == null) {
            log.error("无效处理器:{}", wrapper);
            ctx.writeAndFlush(HttpUtils.buildFullHttpResponse(serializer.serialize(wrapper), null));
            return;
        }
        final HandleContext handleContext = new HandleContext(serializer, wrapper.getInfo(), ctx.channel());
        try {
            iTaskHandler.handle(handleContext);
        } catch (Exception e) {
            log.error("处理异常:{}", e);
            final ClientHeartbeatRespInfo clientHeartbeatInfo = new ClientHeartbeatRespInfo(FAILED_STATUS, e.getMessage());
            final InfoWrapper infoWrapper = new InfoWrapper(clientHeartbeatInfo, wrapper.getHandler());
            ctx.writeAndFlush(HttpUtils.buildFullHttpResponse(serializer.serialize(infoWrapper),
                    null));
        }
    }

    private void initHandlerMap() {
        handlerMap.put(REGISTER_HANDLE, new ClientRegisterHandler(configuration.getClientPool()));
        handlerMap.put(HEARTBEAT_HANDLE, new ClientHeartbeatHandler());
    }
}
