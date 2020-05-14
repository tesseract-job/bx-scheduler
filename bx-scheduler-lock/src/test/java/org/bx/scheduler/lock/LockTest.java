package org.bx.scheduler.lock;

import com.alibaba.druid.pool.DruidDataSource;
import org.bx.scheduler.lock.mysql.MysqlFUDistributeLock;
import org.bx.scheduler.lock.mysql.MysqlIDDistributeLock;
import org.bx.scheduler.lock.redis.RedisNXDistributeLock;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class LockTest {
    private DataSource dataSource;
    private String key = "test_lock";
    private JedisPool jedisPool;
    private ExecutorService executorService = Executors.newCachedThreadPool();

    @Before
    public void before() throws Exception {
        initDataSource();
        initJedisPool();
    }

    private void initJedisPool() {
        final JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(50);
        jedisPoolConfig.setMinIdle(50);
        jedisPool = new JedisPool(jedisPoolConfig, "192.168.174.101", 6379);
    }

    private void initDataSource() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl("jdbc:mysql://127.0.0.1:3306/test?characterEncoding=utf8&useSSL=false&serverTimezone=UTC");
        druidDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        druidDataSource.setPassword("root");
        druidDataSource.setUsername("root");
//        druidDataSource.setMaxActive(500);
//        druidDataSource.setInitialSize(500);
        druidDataSource.init();
        dataSource = druidDataSource;
    }

    @Test
    public void testMysqlFUDistributeLock() throws Exception {
        int[] count = {0};
        for (int i = 0; i < 1000; i++) {
            executorService.submit(() -> {
                final MysqlFUDistributeLock lock = new MysqlFUDistributeLock(dataSource);
                try {
                    lock.lock(key);
//                    if (lock.tryLock(schedulerLockInfo, 1000, TimeUnit.MILLISECONDS)) {
                    count[0]++;
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        lock.unLock(key);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);
        System.out.println(count[0]);
    }

    @Test
    public void testMysqlIDDistributeLock() throws Exception {
        int[] count = {0};

        for (int i = 0; i < 1000; i++) {
            executorService.submit(() -> {
                final MysqlIDDistributeLock lock = new MysqlIDDistributeLock(dataSource, 10);
                try {
                    lock.lock(key);
//                    if (lock.tryLock(schedulerLockInfo, 1000, TimeUnit.MILLISECONDS)) {
                    count[0]++;
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        lock.unLock(key);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);
        System.out.println(count[0]);
    }

    @Test
    public void testJedis() {
        final Jedis client = jedisPool.getResource();
        client.set("test", "test");
        System.out.println(client.get("test"));
        client.del("test");
    }


    @Test
    public void testRedisNXDistributeLock() throws Exception {
        int[] count = {0};
        final AtomicInteger atomicInteger = new AtomicInteger();
        for (int i = 0; i < 1000; i++) {
            executorService.submit(() -> {
                final RedisNXDistributeLock lock = new RedisNXDistributeLock(jedisPool, 3 * 1000L,
                        atomicInteger.getAndIncrement() + "");
                try {
                    lock.lock(key);
//                    if (lock.tryLock(schedulerLockInfo, 1000, TimeUnit.MILLISECONDS)) {
                    count[0]++;
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        lock.unLock(key);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);
        System.out.println(count[0]);
    }

}
