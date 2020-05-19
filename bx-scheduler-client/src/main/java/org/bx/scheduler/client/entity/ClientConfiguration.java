package org.bx.scheduler.client.entity;

import lombok.Data;
import org.bx.scheduler.common.serializer.ISerializer;

@Data
public class ClientConfiguration {
    private int clientServerPort;
    private ISerializer serializer;
}
