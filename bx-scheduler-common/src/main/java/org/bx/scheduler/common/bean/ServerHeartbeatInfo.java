package org.bx.scheduler.common.bean;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ServerHeartbeatInfo {
    private String host;
    private int port;
}
