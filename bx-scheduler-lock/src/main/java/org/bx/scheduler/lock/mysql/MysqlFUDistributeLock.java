package org.bx.scheduler.lock.mysql;

import lombok.SneakyThrows;
import org.bx.scheduler.lock.AbstractDistributeLock;
import org.bx.scheduler.lock.IDistributeLock;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * fu(for update)锁，可重入基于行锁，不支持行锁的无效或锁表，支持阻塞和非阻塞
 */
public class MysqlFUDistributeLock extends AbstractDistributeLock implements IDistributeLock {
    public static final String SELECT_SQL = "select * from fud_distribute_lock where `lock_name`=?  for update";
    private final DataSource dataSource;
    private ExecutorService threadPoolExecutor = Executors.newSingleThreadExecutor();
    private Connection connection;

    public MysqlFUDistributeLock(DataSource dataSource, String lockName) {
        super(lockName);
        this.dataSource = dataSource;
    }

    @SneakyThrows
    @Override
    public void lock() {
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(SELECT_SQL)) {
                statement.setString(1, lockName());
                statement.executeQuery();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) {
        final Future<?> future = threadPoolExecutor.submit(() -> {
            try {
                lock();
                return 1;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        try {
            final Object o = future.get(time, unit);
            if (o == null) {
                future.cancel(true);
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @SneakyThrows
    @Override
    public void unlock() {
        connection.commit();
        connection.close();
        connection = null;
    }


}
