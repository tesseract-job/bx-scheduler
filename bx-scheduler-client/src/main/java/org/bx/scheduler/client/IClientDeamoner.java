package org.bx.scheduler.client;

import org.bx.scheduler.common.bean.RegisterInfo;

import java.util.List;

public interface IClientDeamoner {
    void register(List<RegisterInfo> registerInfoList);

    void heartbeat();
}
