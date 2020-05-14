package org.bx.scheduler.lock.redis;

import lombok.extern.slf4j.Slf4j;
import org.bx.scheduler.lock.AbstractDistributeLock;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * redis 单节点锁，基于NX
 */
@Slf4j
public class RedisNXDistributeLock extends AbstractDistributeLock {
    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    private JedisPool jedisPool;
    //毫秒
    private long expireTime;
    private String requestId;
    private ExecutorService threadPoolExecutor = Executors.newSingleThreadExecutor();

    public RedisNXDistributeLock(JedisPool jedisPool, long expireTime, String requestId) {
        this.jedisPool = jedisPool;
        this.expireTime = expireTime;
        this.requestId = requestId;
    }

    @Override
    public void lock(String key) throws Exception {
        final Jedis client = jedisPool.getResource();
        for (; ; ) {
            final String resp = client.set(key, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
            if (LOCK_SUCCESS.equals(resp)) {
                client.close();
                break;
            }
            Thread.sleep(1000);
        }
    }

    @Override
    public boolean trylock(String key) throws Exception {
        final Jedis client = jedisPool.getResource();
        final String resp = client.set(key, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
        if (LOCK_SUCCESS.equals(resp)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean tryLock(String key, long time, TimeUnit unit) throws Exception {
        final Future<?> future = threadPoolExecutor.submit(() -> {
            try {
                lock(key);
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
    public void unLock(String key) throws Exception {
        final Jedis client = jedisPool.getResource();
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        client.eval(script, Collections.singletonList(key), Collections.singletonList(requestId));
        client.close();
    }
}
