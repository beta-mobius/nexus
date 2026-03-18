package io.modular.framework.api;

/**
 * 事务管理器API
 * 替代Spring的PlatformTransactionManager，提供统一的事务管理接口
 */
public interface TransactionManager {
    
    /**
     * 获取事务状态
     * @param definition 事务定义
     * @return 事务状态
     */
    TransactionStatus getTransaction(TransactionDefinition definition);
    
    /**
     * 提交事务
     * @param status 事务状态
     */
    void commit(TransactionStatus status);
    
    /**
     * 回滚事务
     * @param status 事务状态
     */
    void rollback(TransactionStatus status);
    
    /**
     * 执行事务性操作（编程式事务）
     * @param callback 事务回调
     * @param <T> 返回值类型
     * @return 回调执行结果
     */
    <T> T execute(TransactionCallback<T> callback);
    
    /**
     * 执行无返回值的事务性操作
     * @param callback 事务回调
     */
    void executeWithoutResult(TransactionCallbackWithoutResult callback);
}