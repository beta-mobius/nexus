package io.modular.framework.driver;

import io.modular.framework.api.*;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

/**
 * Spring事务管理器适配器
 */
public class SpringTransactionManager implements TransactionManager {
    
    private final ApplicationContext applicationContext;
    private PlatformTransactionManager txManager;
    
    public SpringTransactionManager(ApplicationContext context) {
        this.applicationContext = context;
    }
    
    private PlatformTransactionManager getTxManager() {
        if (txManager == null) {
            txManager = applicationContext.getBean(PlatformTransactionManager.class);
        }
        return txManager;
    }
    
    @Override
    public TransactionStatus getTransaction(TransactionDefinition definition) {
        org.springframework.transaction.TransactionDefinition springDef = 
            new SpringTransactionDefinitionAdapter(definition);
        return new SpringTransactionStatusAdapter(
            getTxManager().getTransaction(springDef));
    }
    
    @Override
    public void commit(TransactionStatus status) {
        getTxManager().commit(((SpringTransactionStatusAdapter) status).getStatus());
    }
    
    @Override
    public void rollback(TransactionStatus status) {
        getTxManager().rollback(((SpringTransactionStatusAdapter) status).getStatus());
    }
    
    @Override
    public <T> T execute(TransactionCallback<T> callback) {
        TransactionStatus status = getTransaction(new DefaultTransactionDefinition());
        try {
            T result = callback.doInTransaction();
            commit(status);
            return result;
        } catch (Exception e) {
            rollback(status);
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void executeWithoutResult(TransactionCallbackWithoutResult callback) {
        execute(() -> {
            callback.doInTransaction();
            return null;
        });
    }
    
    private static class SpringTransactionDefinitionAdapter 
            implements org.springframework.transaction.TransactionDefinition {
        
        private final TransactionDefinition definition;
        
        SpringTransactionDefinitionAdapter(TransactionDefinition definition) {
            this.definition = definition;
        }
        
        @Override
        public int getPropagationBehavior() {
            return definition.getPropagationBehavior().ordinal();
        }
        
        @Override
        public int getIsolationLevel() {
            return definition.getIsolationLevel().ordinal();
        }
        
        @Override
        public int getTimeout() {
            return definition.getTimeout();
        }
        
        @Override
        public boolean isReadOnly() {
            return definition.isReadOnly();
        }
        
        @Override
        public String getName() {
            return definition.getName();
        }
    }
    
    private static class SpringTransactionStatusAdapter implements TransactionStatus {
        private final org.springframework.transaction.TransactionStatus status;
        
        SpringTransactionStatusAdapter(org.springframework.transaction.TransactionStatus status) {
            this.status = status;
        }
        
        org.springframework.transaction.TransactionStatus getStatus() {
            return status;
        }
        
        @Override
        public boolean isNewTransaction() {
            return status.isNewTransaction();
        }
        
        @Override
        public boolean hasSavepoint() {
            return status.hasSavepoint();
        }
        
        @Override
        public void setRollbackOnly() {
            status.setRollbackOnly();
        }
        
        @Override
        public boolean isRollbackOnly() {
            return status.isRollbackOnly();
        }
        
        @Override
        public boolean isCompleted() {
            return status.isCompleted();
        }
    }
    
    private static class DefaultTransactionDefinition implements TransactionDefinition {
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
            return -1;
        }
        
        @Override
        public boolean isReadOnly() {
            return false;
        }
        
        @Override
        public String getName() {
            return "default";
        }
    }
}