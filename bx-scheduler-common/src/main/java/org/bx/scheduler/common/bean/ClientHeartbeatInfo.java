package org.bx.scheduler.common.bean;

import lombok.Data;

@Data
public class ClientHeartbeatInfo {
    private String host;
    private int port;
}
