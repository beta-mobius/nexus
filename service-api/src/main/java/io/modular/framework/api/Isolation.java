package io.modular.framework.api;

/**
 * 事务隔离级别枚举
 */
public enum Isolation {
    
    /**
     * 使用底层数据源的默认隔离级别
     */
    DEFAULT,
    
    /**
     * 读未提交（最低隔离级别，允许脏读）
     */
    READ_UNCOMMITTED,
    
    /**
     * 读已提交（防止脏读，允许不可重复读和幻读）
     */
    READ_COMMITTED,
    
    /**
     * 可重复读（防止脏读和不可重复读，允许幻读）
     */
    REPEATABLE_READ,
    
    /**
     * 串行化（最高隔离级别，防止所有并发问题）
     */
    SERIALIZABLE
}