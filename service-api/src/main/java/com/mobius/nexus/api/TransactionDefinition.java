package com.mobius.nexus.api;

/**
 * 事务定义接口
 */
public interface TransactionDefinition {
    
    /**
     * 获取传播行为
     */
    Propagation getPropagationBehavior();
    
    /**
     * 获取隔离级别
     */
    Isolation getIsolationLevel();
    
    /**
     * 获取超时时间（秒�?     */
    int getTimeout();
    
    /**
     * 是否为只读事�?     */
    boolean isReadOnly();
    
    /**
     * 获取事务名称
     */
    String getName();
}