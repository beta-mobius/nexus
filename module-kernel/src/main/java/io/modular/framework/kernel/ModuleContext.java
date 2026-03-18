package io.modular.framework.kernel;

import io.modular.framework.api.BeanContainer;

/**
 * 模块上下文接口
 * 为模块提供运行时环境和服务
 */
public interface ModuleContext {
    
    /**
     * 获取模块实例
     */
    Module getModule();
    
    /**
     * 获取Bean容器
     */
    BeanContainer getBeanContainer();
    
    /**
     * 注册服务
     * @param service 服务实例
     * @param serviceTypes 服务接口类型
     * @return 服务注册对象，用于取消注册
     */
    ServiceRegistration registerService(Object service, Class<?>... serviceTypes);
    
    /**
     * 获取服务
     * @param serviceType 服务类型
     * @param <T> 服务类型泛型
     * @return 服务实例，如果不存在返回null
     */
    <T> T getService(Class<T> serviceType);
    
    /**
     * 获取模块配置
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    String getProperty(String key, String defaultValue);
    
    /**
     * 获取模块配置
     * @param key 配置键
     * @return 配置值，如果不存在返回null
     */
    String getProperty(String key);
}