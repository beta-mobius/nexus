package io.modular.framework.api;

/**
 * 应用监听器接口
 * @param <E> 事件类型
 */
@FunctionalInterface
public interface ApplicationListener<E> {
    
    /**
     * 处理应用事件
     * @param event 事件对象
     */
    void onApplicationEvent(E event);
}