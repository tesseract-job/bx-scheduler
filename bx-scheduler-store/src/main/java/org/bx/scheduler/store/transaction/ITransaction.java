package org.bx.scheduler.store.transaction;

public interface ITransaction {
    void startTransaction(TransactionContext context);

    void commit(TransactionContext context);

    void rollback(TransactionContext context);
}
