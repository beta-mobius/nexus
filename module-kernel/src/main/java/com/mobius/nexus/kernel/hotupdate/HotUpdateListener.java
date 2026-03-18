package com.mobius.nexus.kernel.hotupdate;

/**
 * 热更新监听器接口
 * 用于监听模块热更新事件
 */
public interface HotUpdateListener {
    
    /**
     * 处理热更新事件
     * @param event 热更新事件
     */
    void onHotUpdateEvent(HotUpdateEvent event);
    
    /**
     * 判断是否处理指定类型的事件
     * @param eventType 事件类型
     * @return 是否处理
     */
    default boolean shouldHandle(HotUpdateEvent.Type eventType) {
        return true; // 默认处理所有事件
    }
}
