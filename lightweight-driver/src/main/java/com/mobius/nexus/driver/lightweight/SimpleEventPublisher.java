package com.mobius.nexus.driver.lightweight;

import com.mobius.nexus.api.ApplicationListener;
import com.mobius.nexus.api.EventPublisher;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

/**
 * 简单事件发布器实现
 * 支持同步和异步事件发�? */
public class SimpleEventPublisher implements EventPublisher {
    
    private final Map<Class<?>, List<ApplicationListener<?>>> listeners = new ConcurrentHashMap<>();
    private final Map<ApplicationListener<?>, Class<?>> listenerTypes = new ConcurrentHashMap<>();
    private Executor executor;
    
    @Override
    public void publishEvent(Object event) {
        if (event == null) {
            return;
        }
        
        Class<?> eventType = event.getClass();
        List<ApplicationListener<?>> eventListeners = listeners.get(eventType);
        
        if (eventListeners != null) {
            for (ApplicationListener<?> listener : eventListeners) {
                invokeListener(listener, event);
            }
        }
        
        // 同时通知父类事件的监听器
        for (Map.Entry<Class<?>, List<ApplicationListener<?>>> entry : listeners.entrySet()) {
            if (entry.getKey().isAssignableFrom(eventType) && entry.getKey() != eventType) {
                for (ApplicationListener<?> listener : entry.getValue()) {
                    invokeListener(listener, event);
                }
            }
        }
    }
    
    @Override
    public void publishEventAsync(Object event) {
        if (executor == null) {
            // 如果没有设置执行器，使用同步发布
            publishEvent(event);
        } else {
            executor.execute(() -> publishEvent(event));
        }
    }
    
    @Override
    public void publishEventAsync(Object event, Executor executor) {
        executor.execute(() -> publishEvent(event));
    }
    
    @SuppressWarnings("unchecked")
    private <E> void invokeListener(ApplicationListener<?> listener, Object event) {
        try {
            ((ApplicationListener<E>) listener).onApplicationEvent((E) event);
        } catch (ClassCastException e) {
            // 忽略类型不匹配的监听�?        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void addApplicationListener(ApplicationListener<?> listener) {
        // 通过反射获取监听器的事件类型
        Class<?> listenerType = getListenerEventType(listener);
        if (listenerType == null) {
            throw new IllegalArgumentException("Cannot determine event type for listener: " + listener);
        }
        
        listeners.computeIfAbsent(listenerType, k -> new CopyOnWriteArrayList<>())
                 .add(listener);
        listenerTypes.put(listener, listenerType);
        
        System.out.println("Listener registered for event type: " + listenerType.getName());
    }
    
    @Override
    public void removeApplicationListener(ApplicationListener<?> listener) {
        Class<?> listenerType = listenerTypes.remove(listener);
        if (listenerType != null) {
            List<ApplicationListener<?>> eventListeners = listeners.get(listenerType);
            if (eventListeners != null) {
                eventListeners.remove(listener);
                if (eventListeners.isEmpty()) {
                    listeners.remove(listenerType);
                }
            }
        }
    }
    
    /**
     * 设置异步执行�?     */
    public void setExecutor(Executor executor) {
        this.executor = executor;
    }
    
    /**
     * 获取监听器的事件类型
     */
    private Class<?> getListenerEventType(ApplicationListener<?> listener) {
        // 通过反射分析监听器的泛型类型
        // 简化实现：查找实现接口中的泛型参数
        
        Class<?> clazz = listener.getClass();
        java.lang.reflect.Type[] genericInterfaces = clazz.getGenericInterfaces();
        
        for (java.lang.reflect.Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof java.lang.reflect.ParameterizedType) {
                java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) genericInterface;
                if (pt.getRawType() == ApplicationListener.class) {
                    java.lang.reflect.Type[] typeArgs = pt.getActualTypeArguments();
                    if (typeArgs.length == 1 && typeArgs[0] instanceof Class) {
                        return (Class<?>) typeArgs[0];
                    }
                }
            }
        }
        
        // 如果找不到泛型信息，尝试从父类查�?        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && ApplicationListener.class.isAssignableFrom(superClass)) {
            return getListenerEventType((ApplicationListener<?>) superClass.cast(listener));
        }
        
        return null;
    }
}