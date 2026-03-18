package io.modular.framework.api;

/**
 * 无返回值的事务回调接口
 */
@FunctionalInterface
public interface TransactionCallbackWithoutResult {
    
    /**
     * 在事务上下文中执行
     */
    void doInTransaction();
}