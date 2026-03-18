package io.modular.framework.api;

import java.util.List;
import java.util.Map;

/**
 * Bean容器API
 * 替代Spring的ApplicationContext，提供统一的Bean管理接口
 */
public interface BeanContainer {
    
    /**
     * 注册Bean类
     * @param beanClass Bean类型
     * @param <T> Bean类型泛型
     */
    <T> void registerBean(Class<T> beanClass);
    
    /**
     * 注册Bean类（指定名称）
     * @param beanClass Bean类型
     * @param name Bean名称
     * @param <T> Bean类型泛型
     */
    <T> void registerBean(Class<T> beanClass, String name);
    
    /**
     * 注册Bean实例（指定名称）
     * @param instance Bean实例
     * @param name Bean名称
     * @param <T> Bean类型泛型
     */
    <T> void registerBean(T instance, String name);
    
    /**
     * 获取Bean（按类型）
     * @param beanClass Bean类型
     * @param <T> Bean类型泛型
     * @return Bean实例
     */
    <T> T getBean(Class<T> beanClass);
    
    /**
     * 获取Bean（按名称和类型）
     * @param name Bean名称
     * @param beanClass Bean类型
     * @param <T> Bean类型泛型
     * @return Bean实例
     */
    <T> T getBean(String name, Class<T> beanClass);
    
    /**
     * 获取指定类型的所有Bean
     * @param beanClass Bean类型
     * @param <T> Bean类型泛型
     * @return Bean名称到实例的映射
     */
    <T> Map<String, T> getBeansOfType(Class<T> beanClass);
    
    /**
     * 获取Bean定义
     * @param name Bean名称
     * @return Bean定义
     */
    BeanDefinition getBeanDefinition(String name);
    
    /**
     * 获取所有Bean定义名称
     * @return Bean定义名称列表
     */
    List<String> getBeanDefinitionNames();
    
    /**
     * 注册作用域
     * @param scopeName 作用域名称
     * @param scope 作用域实现
     */
    void registerScope(String scopeName, Scope scope);
    
    /**
     * 刷新容器
     */
    void refresh();
    
    /**
     * 关闭容器
     */
    void close();
}