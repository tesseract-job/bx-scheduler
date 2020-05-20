package org.bx.scheduler.server.handler;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bx.scheduler.common.bean.ClientRegisterInfo;
import org.bx.scheduler.common.bean.ClientRegisterRespInfo;
import org.bx.scheduler.common.bean.InfoWrapper;
import org.bx.scheduler.common.util.HttpUtils;
import org.bx.scheduler.dispatcher.IClientPool;
import org.bx.scheduler.dispatcher.netty.http.NettyHttpClient;
import org.bx.scheduler.server.HandleContext;

import static org.bx.scheduler.common.constant.CommonConstant.REGISTER_HANDLE;
import static org.bx.scheduler.common.constant.CommonConstant.SUCCESS_STATUS;

/**
 * 客户端注册
 */
@Slf4j
@AllArgsConstructor
public class ClientRegisterHandler implements ITaskHandler {
    private IClientPool clientPool;

    @Override
    public void handle(HandleContext context) {
        final JSONObject contextInfo = (JSONObject) context.getInfo();
        log.info("接收到注册:{}", contextInfo);
        final ClientRegisterInfo registerInfo = contextInfo.toJavaObject(ClientRegisterInfo.class);
        final Channel channel = context.getChannel();
        final ClientRegisterRespInfo clientRegisterRespInfo = doHandle(registerInfo, channel);
        final InfoWrapper infoWrapper = new InfoWrapper(clientRegisterRespInfo, REGISTER_HANDLE);
        final byte[] serialize = context.getSerializer().serialize(infoWrapper);
        final FullHttpResponse fullHttpResponse = HttpUtils.buildFullHttpResponse(serialize, null);
        channel.writeAndFlush(fullHttpResponse);
    }

    private ClientRegisterRespInfo doHandle(ClientRegisterInfo registerInfo, Channel channel) {
        ClientRegisterRespInfo clientRegisterRespInfo = new ClientRegisterRespInfo(SUCCESS_STATUS, null);
        final String socket = registerInfo.getHost() + "" + registerInfo.getPort();
        final NettyHttpClient nettyHttpClient = new NettyHttpClient(socket,channel);
        clientPool.storeClient(socket,nettyHttpClient);
        return clientRegisterRespInfo;
    }
}
