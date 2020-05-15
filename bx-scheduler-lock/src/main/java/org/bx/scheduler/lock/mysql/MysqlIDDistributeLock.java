package org.bx.scheduler.lock.mysql;

import lombok.extern.slf4j.Slf4j;
import org.bx.scheduler.lock.AbstractDistributeLock;
import org.bx.scheduler.lock.IDistributeLock;
import org.bx.scheduler.lock.WatchDog;

import javax.sql.DataSource;
import java.sql.*;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 不可重入,目前引擎最好用myisam，innodb 由于gap和意向锁会造成死锁，rollback 性能下降，且机器down掉 造成死锁
 * create table id_distribute_lock(
 * id int unsigned auto_increment primary key,
 * lock_name varchar(100) not null,
 * expire_time bigint not null,
 * thread_id varchar(100) not null,
 * unique(lock_name)
 * ) engine=myisam;
 */
@Slf4j
public class MysqlIDDistributeLock extends AbstractDistributeLock implements IDistributeLock {
    private static final String SELETE_SQL_FORMAT = "select * from id_distribute_lock where lock_name=?";
    private static final String UPDATE_SQL_FORMAT = "update id_distribute_lock set expire_time=? where lock_name=?";
    private static final String INSERT_SQL_FORMAT = "insert into id_distribute_lock(lock_name,expire_time,thread_id) values(?,?,?)";
    private static final String DELETE_SQL_FORMAT = "delete from id_distribute_lock where lock_name=?";
    private final DataSource dataSource;
    private ExecutorService threadPoolExecutor = Executors.newSingleThreadExecutor();
    private int expireTime;
    private long reletTime;
    private WatchDog watchDog;
    private String threadId;

    public MysqlIDDistributeLock(DataSource dataSource, int expireTime, String lockName) {
        super(lockName);
        this.dataSource = dataSource;
        this.expireTime = expireTime;
        this.reletTime = this.expireTime / 2;
        threadId = Thread.currentThread().getId() + "-" + UUID.randomUUID().toString();
        watchDog = new WatchDog(() -> reletAndCheck(), reletTime, TimeUnit.MILLISECONDS);
    }

    @Override
    public void lock() {
        watchDog.start();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL_FORMAT)) {
            statement.setString(1, this.lockName());
            statement.setLong(2, System.currentTimeMillis() + expireTime);
            statement.setString(3, this.lockName());
            while (true) {
                try {
                    //获取锁成功
                    if (statement.executeUpdate() > 0) {
                        break;
                    }
                } catch (SQLIntegrityConstraintViolationException e) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //睡眠100ms继续获取锁，锁力度较大可修改这个时间
                Thread.sleep(100);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void reletAndCheck() {
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        try {
            final Connection connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(SELETE_SQL_FORMAT);
            preparedStatement.setString(1, this.lockName());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                final String thread_id = resultSet.getString("thread_id");
                final long expire_time = resultSet.getLong("expire_time");
                log.debug("thread_id:{}", thread_id);
                if (this.threadId.equals(thread_id)) {
                    //续租
                    try (PreparedStatement updatePreparedStatement = connection.prepareStatement(UPDATE_SQL_FORMAT)) {
                        updatePreparedStatement.setLong(1, expire_time + reletTime);
                        updatePreparedStatement.setString(2, this.lockName());
                        updatePreparedStatement.executeUpdate();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    //检查时间是否过期
                    if (System.currentTimeMillis() > expire_time) {
                        delete();
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void delete() {
        try (PreparedStatement statement = this.dataSource.getConnection().prepareStatement(DELETE_SQL_FORMAT)) {
            statement.setString(1, this.lockName());
            statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) {
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
        watchDog.stop();
        delete();
    }

}
