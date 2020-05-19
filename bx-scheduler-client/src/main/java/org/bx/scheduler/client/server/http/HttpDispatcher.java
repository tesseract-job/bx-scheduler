package org.bx.scheduler.client.server.http;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.bx.scheduler.client.entity.ClientConfiguration;
import org.bx.scheduler.client.server.HandleContext;
import org.bx.scheduler.client.server.handler.ITaskHandler;
import org.bx.scheduler.common.bean.ClientHeartbeatInfo;
import org.bx.scheduler.common.bean.InfoWrapper;
import org.bx.scheduler.common.serializer.ISerializer;
import org.bx.scheduler.common.util.HttpUtils;

import java.util.HashMap;
import java.util.Map;

import static org.bx.scheduler.common.constant.CommonConstant.FAILED_STATUS;

@Slf4j
public class HttpDispatcher extends ChannelInboundHandlerAdapter {
    private static final Map<String, ITaskHandler> handlerMap = new HashMap<>(8);

    static {

    }

    private ClientConfiguration configuration;


    public HttpDispatcher(ClientConfiguration configuration) {
        this.configuration = configuration;
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
            final ClientHeartbeatInfo clientHeartbeatInfo = new ClientHeartbeatInfo();
            clientHeartbeatInfo.setMsg(e.getMessage());
            clientHeartbeatInfo.setStatus(FAILED_STATUS);
            final InfoWrapper infoWrapper = new InfoWrapper();
            infoWrapper.setHandler("heartBeat");
            infoWrapper.setInfo(clientHeartbeatInfo);
            ctx.writeAndFlush(HttpUtils.buildFullHttpResponse(serializer.serialize(infoWrapper),
                    null));
        }
    }
}
