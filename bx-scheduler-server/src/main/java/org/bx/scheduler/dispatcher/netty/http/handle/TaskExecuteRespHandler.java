package org.bx.scheduler.dispatcher.netty.http.handle;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import org.bx.scheduler.dispatcher.netty.IClientInfoHandle;
import org.bx.scheduler.common.bean.InfoWrapper;
import org.bx.scheduler.common.bean.TaskExecuteRespInfo;

@AllArgsConstructor
public class TaskExecuteRespHandler implements IClientInfoHandle {
    private FutureCountDownLatchStore store;

    @Override
    public void handle(InfoWrapper wrapper) {
        final TaskExecuteRespInfo taskExecuteRespInfo = ((JSONObject) wrapper.getInfo()).toJavaObject(TaskExecuteRespInfo.class);
        wrapper.setInfo(taskExecuteRespInfo);
        store.wakeup(taskExecuteRespInfo.getFutureId(), wrapper);
    }
}
