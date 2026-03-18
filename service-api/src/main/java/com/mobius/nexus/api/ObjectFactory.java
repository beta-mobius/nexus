package com.mobius.nexus.api;

/**
 * 对象工厂接口
 * @param <T> 对象类型
 */
@FunctionalInterface
public interface ObjectFactory<T> {
    
    /**
     * 获取对象实例
     */
    T getObject();
}