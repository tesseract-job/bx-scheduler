package org.bx.scheduler.common.util;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;

import java.net.URI;
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

    public static FullHttpRequest buildFullHttpRequest(URI uri, byte[] content, Consumer<FullHttpRequest> callback) {
        DefaultFullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,
                uri.toASCIIString(), Unpooled.wrappedBuffer(content));
        if (callback != null) {
            callback.accept(httpRequest);
        }
        return httpRequest;
    }

    public static FullHttpRequest buildDefaultFullHttpRequest(URI uri, byte[] content) {
        FullHttpRequest httpRequest = HttpUtils.buildFullHttpRequest(uri, content, (fullHttpRequest) -> {
            fullHttpRequest.headers().set(HttpHeaderNames.HOST, uri.getHost());
            fullHttpRequest.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            fullHttpRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, fullHttpRequest.content().readableBytes());
        });
        return httpRequest;
    }


    public static String getURLPath(String url) {
        return url.substring(url.lastIndexOf("/"));
    }

}