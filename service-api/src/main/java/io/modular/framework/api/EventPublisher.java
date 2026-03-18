package io.modular.framework.api;

import java.util.concurrent.Executor;

/**
 * 事件发布器API
 * 替代Spring的ApplicationEventPublisher，提供统一的事件发布接口
 */
public interface EventPublisher {
    
    /**
     * 发布事件
     * @param event 事件对象
     */
    void publishEvent(Object event);
    
    /**
     * 异步发布事件（使用默认执行器）
     * @param event 事件对象
     */
    void publishEventAsync(Object event);
    
    /**
     * 异步发布事件（指定执行器）
     * @param event 事件对象
     * @param executor 执行器
     */
    void publishEventAsync(Object event, Executor executor);
    
    /**
     * 添加应用监听器
     * @param listener 监听器
     */
    void addApplicationListener(ApplicationListener<?> listener);
    
    /**
     * 移除应用监听器
     * @param listener 监听器
     */
    void removeApplicationListener(ApplicationListener<?> listener);
}