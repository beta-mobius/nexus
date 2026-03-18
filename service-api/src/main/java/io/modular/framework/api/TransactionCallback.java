package io.modular.framework.api;

/**
 * 事务回调接口（有返回值）
 * @param <T> 返回值类型
 */
@FunctionalInterface
public interface TransactionCallback<T> {
    
    /**
     * 在事务上下文中执行
     * @return 执行结果
     */
    T doInTransaction();
}