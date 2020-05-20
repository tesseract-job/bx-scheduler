package org.bx.scheduler.server.handler;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.bx.scheduler.common.bean.ClientHeartbeatInfo;
import org.bx.scheduler.common.bean.ClientHeartbeatRespInfo;
import org.bx.scheduler.common.bean.InfoWrapper;
import org.bx.scheduler.common.util.HttpUtils;
import org.bx.scheduler.server.HandleContext;

import static org.bx.scheduler.common.constant.CommonConstant.HEARTBEAT_HANDLE;
import static org.bx.scheduler.common.constant.CommonConstant.SUCCESS_STATUS;

/**
 * 客户端心跳
 */
@Slf4j
public class ClientHeartbeatHandler implements ITaskHandler {
    @Override
    public void handle(HandleContext context) {
        final JSONObject contextInfo = (JSONObject) context.getInfo();
        log.info("接收到心跳:{}", contextInfo);
        final ClientHeartbeatInfo clientHeartbeatInfo = contextInfo.toJavaObject(ClientHeartbeatInfo.class);
        final Channel channel = context.getChannel();
        final ClientHeartbeatRespInfo clientHeartbeatRespInfo = new ClientHeartbeatRespInfo(SUCCESS_STATUS, null);
        final InfoWrapper infoWrapper = new InfoWrapper(clientHeartbeatRespInfo, HEARTBEAT_HANDLE);
        final byte[] serialize = context.getSerializer().serialize(infoWrapper);
        final FullHttpResponse fullHttpResponse = HttpUtils.buildFullHttpResponse(serialize, null);
        channel.writeAndFlush(fullHttpResponse);

    }
}
