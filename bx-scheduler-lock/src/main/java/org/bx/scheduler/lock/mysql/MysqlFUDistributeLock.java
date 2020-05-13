package org.bx.scheduler.lock.mysql;

import org.bx.scheduler.lock.AbstractDistributeLock;
import org.bx.scheduler.lock.IDistributeLock;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
    private Connection connection;

    private ExecutorService threadPoolExecutor = Executors.newSingleThreadExecutor();

    public MysqlFUDistributeLock(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void lock(String key) throws Exception {
        checkConnection();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_SQL)) {
            statement.setString(1, key);
            statement.executeQuery();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
        connection.commit();
        connection.close();
        connection = null;
    }


    private void checkConnection() throws SQLException {
        if (connection == null) {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
        }
    }
}
