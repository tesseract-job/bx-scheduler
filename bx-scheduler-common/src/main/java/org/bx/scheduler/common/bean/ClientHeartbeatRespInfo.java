package org.bx.scheduler.common.bean;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ClientHeartbeatRespInfo {
    private int status;
    private String msg;
}
