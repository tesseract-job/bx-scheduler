package org.bx.scheduler.client.entity;

import lombok.Data;
import org.bx.scheduler.client.buddy.IClientBuddy;
import org.bx.scheduler.common.serializer.ISerializer;

import java.net.URI;

@Data
public class ClientConfiguration {
    private int clientServerPort;
    private ISerializer serializer;
    private URI serverURI;
    private URI localURI;
    private IClientBuddy clientBuddy;
    private int heartbeatTimeInterval;
    private int maxExecutorPoolSize;
    private int coreExecutorPoolSize;
    private int blockQueueSize;
}
