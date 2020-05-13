package org.bx.scheduler.lock;

import java.util.concurrent.TimeUnit;

public class WatchDog {
    private volatile boolean isStop;
    private long watchTime;
    private Runnable target;
    private Thread thread;

    public WatchDog(Runnable target, long watchTime, TimeUnit timeUnit) {
        this.watchTime = timeUnit.toMillis(watchTime);
        this.target = target;
        this.thread = new Thread(() -> {
            while (!isStop) {
                this.target.run();
                try {
                    Thread.sleep(this.watchTime);
                } catch (InterruptedException e) {
                }
            }
        }, "lock-watch-dog");
    }

    public void start() {
        thread.start();
    }

    public void stop() {
        this.isStop = true;
        thread.interrupt();
    }
}
