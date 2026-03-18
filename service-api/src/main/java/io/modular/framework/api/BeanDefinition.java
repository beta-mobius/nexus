package io.modular.framework.api;

import java.util.List;

/**
 * Bean定义
 */
public interface BeanDefinition {
    
    /**
     * 获取Bean名称
     */
    String getName();
    
    /**
     * 获取Bean类型
     */
    Class<?> getBeanClass();
    
    /**
     * 获取作用域
     */
    String getScope();
    
    /**
     * 是否为单例
     */
    boolean isSingleton();
    
    /**
     * 是否延迟初始化
     */
    boolean isLazyInit();
    
    /**
     * 获取属性值列表
     */
    List<PropertyValue> getPropertyValues();
    
    /**
     * 获取构造器参数值
     */
    ConstructorArgumentValues getConstructorArgumentValues();
}