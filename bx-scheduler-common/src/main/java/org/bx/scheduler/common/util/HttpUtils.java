package org.bx.scheduler.common.util;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;

import java.util.function.Consumer;

public class HttpUtils {
    public static FullHttpResponse buildFullHttpResponse(byte[] content, Consumer<FullHttpResponse> callback) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK
                , Unpooled.copiedBuffer(content));
        response.headers().add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.BINARY);
        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, content.length);
        if (callback != null) {
            callback.accept(response);
        }
        return response;
    }


    public static FullHttpRequest buildDefaultFullHttpRequest(String host, String path, byte[] content) {
        DefaultFullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,
                path, Unpooled.wrappedBuffer(content));
        httpRequest.headers().set(HttpHeaderNames.HOST, host);
        httpRequest.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        httpRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpRequest.content().readableBytes());
        return httpRequest;
    }

}