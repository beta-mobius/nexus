package io.modular.framework.api;

/**
 * 事务状态接口
 */
public interface TransactionStatus {
    
    /**
     * 是否是新事务
     */
    boolean isNewTransaction();
    
    /**
     * 是否有保存点
     */
    boolean hasSavepoint();
    
    /**
     * 设置为仅回滚
     */
    void setRollbackOnly();
    
    /**
     * 是否仅回滚
     */
    boolean isRollbackOnly();
    
    /**
     * 事务是否已完成
     */
    boolean isCompleted();
}