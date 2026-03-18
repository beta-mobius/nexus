package io.modular.framework.driver.lightweight;

import io.modular.framework.api.*;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 简单事务管理器实现
 * 适用于不需要真正事务管理的场景（如测试、简单应用）
 */
public class SimpleTransactionManager implements TransactionManager {
    
    private final AtomicInteger transactionCounter = new AtomicInteger(0);
    
    @Override
    public TransactionStatus getTransaction(TransactionDefinition definition) {
        int txId = transactionCounter.incrementAndGet();
        System.out.println("Transaction started: " + txId + ", definition: " + definition);
        
        return new SimpleTransactionStatus(txId, definition);
    }
    
    @Override
    public void commit(TransactionStatus status) {
        SimpleTransactionStatus txStatus = (SimpleTransactionStatus) status;
        if (txStatus.isCompleted()) {
            throw new IllegalTransactionStateException("Transaction already completed: " + txStatus.getTransactionId());
        }
        
        txStatus.setCompleted(true);
        System.out.println("Transaction committed: " + txStatus.getTransactionId());
    }
    
    @Override
    public void rollback(TransactionStatus status) {
        SimpleTransactionStatus txStatus = (SimpleTransactionStatus) status;
        if (txStatus.isCompleted()) {
            throw new IllegalTransactionStateException("Transaction already completed: " + txStatus.getTransactionId());
        }
        
        txStatus.setCompleted(true);
        txStatus.setRollbackOnly(true);
        System.out.println("Transaction rolled back: " + txStatus.getTransactionId());
    }
    
    @Override
    public <T> T execute(TransactionCallback<T> action) {
        TransactionDefinition definition = new SimpleTransactionDefinition();
        TransactionStatus status = getTransaction(definition);
        
        try {
            T result = action.doInTransaction(status);
            commit(status);
            return result;
        } catch (Exception e) {
            rollback(status);
            throw new TransactionException("Transaction execution failed", e);
        }
    }
    
    @Override
    public void execute(TransactionCallbackWithoutResult action) {
        execute((TransactionCallback<Void>) status -> {
            action.doInTransaction(status);
            return null;
        });
    }
    
    /**
     * 简单事务状态实现
     */
    private static class SimpleTransactionStatus implements TransactionStatus {
        private final int transactionId;
        private final TransactionDefinition definition;
        private boolean completed = false;
        private boolean rollbackOnly = false;
        private boolean newTransaction = true;
        
        SimpleTransactionStatus(int transactionId, TransactionDefinition definition) {
            this.transactionId = transactionId;
            this.definition = definition;
        }
        
        int getTransactionId() {
            return transactionId;
        }
        
        boolean isCompleted() {
            return completed;
        }
        
        void setCompleted(boolean completed) {
            this.completed = completed;
        }
        
        void setRollbackOnly(boolean rollbackOnly) {
            this.rollbackOnly = rollbackOnly;
        }
        
        @Override
        public boolean isNewTransaction() {
            return newTransaction;
        }
        
        @Override
        public boolean hasSavepoint() {
            return false;
        }
        
        @Override
        public void setRollbackOnly() {
            this.rollbackOnly = true;
        }
        
        @Override
        public boolean isRollbackOnly() {
            return rollbackOnly;
        }
        
        @Override
        public void flush() {
            // 简单实现，无操作
        }
        
        @Override
        public boolean isCompleted() {
            return completed;
        }
    }
    
    /**
     * 简单事务定义
     */
    private static class SimpleTransactionDefinition implements TransactionDefinition {
        @Override
        public Propagation getPropagationBehavior() {
            return Propagation.REQUIRED;
        }
        
        @Override
        public Isolation getIsolationLevel() {
            return Isolation.DEFAULT;
        }
        
        @Override
        public int getTimeout() {
            return TransactionDefinition.TIMEOUT_DEFAULT;
        }
        
        @Override
        public boolean isReadOnly() {
            return false;
        }
        
        @Override
        public String getName() {
            return "simple-transaction";
        }
    }
}