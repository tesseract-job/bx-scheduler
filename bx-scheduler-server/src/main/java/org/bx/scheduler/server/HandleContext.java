package org.bx.scheduler.server;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bx.scheduler.common.serializer.ISerializer;

@Data
@AllArgsConstructor
public class HandleContext {
    private ISerializer serializer;
    private Object info;
    private Channel channel;
}
