package org.bx.scheduler.log.redis;

import lombok.extern.slf4j.Slf4j;
import org.bx.scheduler.log.AbstractDistributeLock;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * redis 单节点锁，基于NX,不支持锁重入
 * if (redis.call('exists', KEYS[1]) == 0) then " +
 * "redis.call('hincrby', KEYS[1], ARGV[2], 1); " +
 * "redis.call('pexpire', KEYS[1], ARGV[1]); " +
 * "return nil; " +
 * "end; " +
 * "if (redis.call('hexists', KEYS[1], ARGV[2]) == 1) then " +
 * "redis.call('hincrby', KEYS[1], ARGV[2], 1); " +
 * "redis.call('pexpire', KEYS[1], ARGV[1]); " +
 * "return nil; " +
 * "end; " +
 * "return redis.call('pttl', KEYS[1]);",
 */
@Slf4j
public class RedisNXDistributeLock extends AbstractDistributeLock {
    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    private JedisPool jedisPool;
    private long expireTime;
    private long reletTime;
    private long pollTime;
    private String requestId;
    private ExecutorService threadPoolExecutor = Executors.newSingleThreadExecutor();

    public RedisNXDistributeLock(JedisPool jedisPool, int expireTime, String requestId, String lockName) {
        this(jedisPool, expireTime, -1, 100, requestId, lockName);
    }

    public RedisNXDistributeLock(JedisPool jedisPool, int expireTime, long pollTime, String requestId, String lockName) {
        this(jedisPool, expireTime, expireTime / 2, pollTime, requestId, lockName);
    }

    public RedisNXDistributeLock(JedisPool jedisPool, int expireTime, long reletTime, long pollTime, String requestId, String lockName) {
        super(lockName);
        this.jedisPool = jedisPool;
        this.expireTime = expireTime * 1000;
        this.requestId = requestId;
        this.reletTime = reletTime;
        this.pollTime = pollTime;
    }

    @Override
    public void lock() {
        final Jedis client = jedisPool.getResource();
        for (; ; ) {
            final String resp = client.set(this.lockName(), requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
            if (LOCK_SUCCESS.equals(resp)) {
                client.close();
                break;
            }
            try {
                Thread.sleep(pollTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean tryLock(){
        final Jedis client = jedisPool.getResource();
        final String resp = client.set(this.lockName(), requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
        if (LOCK_SUCCESS.equals(resp)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean tryLock( long time, TimeUnit unit) {
        final Future<?> future = threadPoolExecutor.submit(() -> {
            try {
                lock();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        try {
            future.get(time, unit);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void unlock() {
        final Jedis client = jedisPool.getResource();
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        client.eval(script, Collections.singletonList(this.lockName()), Collections.singletonList(requestId));
        client.close();
    }
}
