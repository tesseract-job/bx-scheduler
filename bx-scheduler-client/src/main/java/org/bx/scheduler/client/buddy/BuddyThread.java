package org.bx.scheduler.client.buddy;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bx.scheduler.client.entity.ClientConfiguration;
import org.bx.scheduler.common.bean.TaskInfo;

import java.util.List;

@Slf4j
public class BuddyThread extends Thread {

    private ClientConfiguration clientConfiguration;
    private List<TaskInfo> taskInfoList;

    public BuddyThread(ClientConfiguration clientConfiguration, List<TaskInfo> taskInfoList) {
        super("client-buddy-thread");
        this.setDaemon(true);
        this.clientConfiguration = clientConfiguration;
        this.taskInfoList = taskInfoList;
    }

    @SneakyThrows
    @Override
    public void run() {
        IClientBuddy clientBuddy = clientConfiguration.getClientBuddy();
        log.info("buddy thread start");
        clientBuddy.init();
        clientBuddy.start();
        //注冊重试次数
        for (int i = 0; i < 3; i++) {
            try {
                clientBuddy.register(taskInfoList);
            } catch (Exception e) {
                log.error("注冊失败", e);
            }
        }
        //根据心跳时间，保活心跳
        for (; ; ) {
            clientBuddy.heartbeat();
            Thread.sleep(clientConfiguration.getHeartbeatTimeInterval());
        }
    }
}
