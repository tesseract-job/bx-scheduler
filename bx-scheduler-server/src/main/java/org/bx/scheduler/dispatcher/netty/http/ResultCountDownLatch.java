package org.bx.scheduler.dispatcher.netty.http;

import lombok.Data;
import org.bx.scheduler.common.bean.InfoWrapper;

import java.util.concurrent.CountDownLatch;

@Data
public class ResultCountDownLatch {
    private CountDownLatch countDownLatch;
    private InfoWrapper wrapper;

    public ResultCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }
}
