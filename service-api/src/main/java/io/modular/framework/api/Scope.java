package io.modular.framework.api;

/**
 * 作用域接口
 */
public interface Scope {
    
    /**
     * 从作用域获取对象
     * @param name 对象名称
     * @param objectFactory 对象工厂
     * @return 对象实例
     */
    Object get(String name, ObjectFactory<?> objectFactory);
    
    /**
     * 从作用域移除对象
     * @param name 对象名称
     * @return 被移除的对象，如果不存在返回null
     */
    Object remove(String name);
    
    /**
     * 注册销毁回调
     * @param name 对象名称
     * @param callback 销毁回调
     */
    void registerDestructionCallback(String name, Runnable callback);
}