package org.bx.scheduler.dispatcher.netty.http;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bx.scheduler.common.bean.InfoWrapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class CountDownLatchStore {
    private Map<String, ResultCountDownLatch> countDownLatchMap = new ConcurrentHashMap<>(256);

    @SneakyThrows
    public InfoWrapper run(Runnable runnable, String id) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final ResultCountDownLatch resultCountDownLatch = new ResultCountDownLatch(countDownLatch);
        countDownLatchMap.put(id, resultCountDownLatch);
        runnable.run();
        countDownLatch.await();
        return resultCountDownLatch.getWrapper();
    }


    public void wakeup(String futureId, InfoWrapper wrapper) {
        final ResultCountDownLatch countDownLatch = countDownLatchMap.get(futureId);
        if (countDownLatch != null) {
            countDownLatch.setWrapper(wrapper);
            countDownLatch.getCountDownLatch().countDown();
        } else {
            log.error("找不到需要唤醒的future:{}", futureId);
        }
    }

}
