package org.bx.scheduler.lock;

import com.alibaba.druid.pool.DruidDataSource;
import org.bx.scheduler.lock.mysql.MysqlFUDistributeLock;
import org.bx.scheduler.lock.mysql.MysqlIDDistributeLock;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.SQLException;

public class LockTest {
    private DataSource dataSource;
    private String key = "test_lock";

    @Before
    public void before() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl("jdbc:mysql://127.0.0.1:3306/test?characterEncoding=utf8&useSSL=false&serverTimezone=UTC");
        druidDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        druidDataSource.setPassword("root");
        druidDataSource.setUsername("root");
        druidDataSource.setMaxActive(500);
        druidDataSource.setInitialSize(500);
        druidDataSource.init();
        dataSource = druidDataSource;
    }

    @Test
    public void testMysqlFUDistributeLock() throws Exception {

        int[] count = {0};

        for (int i = 0; i < 1; i++) {
            new Thread(() -> {
                final MysqlFUDistributeLock mysqlFUDistributeLock = new MysqlFUDistributeLock(dataSource);
                try {
                    mysqlFUDistributeLock.lock(key);
//                    if (mysqlFUDistributeLock.tryLock(key, 1000, TimeUnit.MILLISECONDS)) {
                    count[0]++;
//                    } else {
//                        System.out.println(1);
//                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        mysqlFUDistributeLock.unLock(key);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        Thread.sleep(10000);
        System.out.println(count[0]);
    }

    @Test
    public void testMysqlIDDistributeLock() throws Exception {
        int[] count = {0};

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
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
            }).start();
        }
        Thread.sleep(10000);
        System.out.println(count[0]);
    }
}
