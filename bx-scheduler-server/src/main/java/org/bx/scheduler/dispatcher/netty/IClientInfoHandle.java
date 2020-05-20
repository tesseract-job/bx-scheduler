package org.bx.scheduler.dispatcher.netty;

import org.bx.scheduler.common.bean.InfoWrapper;

public interface IClientInfoHandle {
    void handle(InfoWrapper wrapper);
}
