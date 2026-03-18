package io.modular.framework.api;

/**
 * 事务传播行为枚举
 */
public enum Propagation {
    
    /**
     * 支持当前事务，如果不存在则创建新事务
     */
    REQUIRED,
    
    /**
     * 支持当前事务，如果不存在则以非事务方式执行
     */
    SUPPORTS,
    
    /**
     * 必须存在当前事务，否则抛出异常
     */
    MANDATORY,
    
    /**
     * 总是创建新事务，如果存在当前事务则暂停当前事务
     */
    REQUIRES_NEW,
    
    /**
     * 以非事务方式执行，如果存在当前事务则暂停当前事务
     */
    NOT_SUPPORTED,
    
    /**
     * 以非事务方式执行，如果存在当前事务则抛出异常
     */
    NEVER,
    
    /**
     * 如果存在当前事务，则在嵌套事务中执行
     */
    NESTED
}