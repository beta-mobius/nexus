package com.mobius.nexus.driver.lightweight;

import com.mobius.nexus.api.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简单Bean容器实现
 * 基于Map的轻量级容器，支持单例和原型作用�? */
public class SimpleBeanContainer implements BeanContainer {
    
    private final Map<String, BeanDefinition> beanDefinitions = new ConcurrentHashMap<>();
    private final Map<String, Object> singletonBeans = new ConcurrentHashMap<>();
    private final Map<String, ObjectFactory<?>> beanFactories = new ConcurrentHashMap<>();
    
    @Override
    public boolean containsBean(String name) {
        return beanDefinitions.containsKey(name) || singletonBeans.containsKey(name);
    }
    
    @Override
    public Object getBean(String name) {
        BeanDefinition definition = beanDefinitions.get(name);
        if (definition == null) {
            // 检查单例缓�?            Object singleton = singletonBeans.get(name);
            if (singleton != null) {
                return singleton;
            }
            throw new NoSuchBeanException("No bean found with name: " + name);
        }
        
        return createBean(name, definition);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(String name, Class<T> requiredType) {
        Object bean = getBean(name);
        if (requiredType.isInstance(bean)) {
            return (T) bean;
        }
        throw new BeanNotOfRequiredTypeException(
            "Bean named '" + name + "' is not of required type: " + requiredType);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        List<String> matchingNames = new ArrayList<>();
        
        // 查找匹配的Bean定义
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitions.entrySet()) {
            if (requiredType.isAssignableFrom(entry.getValue().getBeanClass())) {
                matchingNames.add(entry.getKey());
            }
        }
        
        // 查找匹配的单例Bean
        for (Map.Entry<String, Object> entry : singletonBeans.entrySet()) {
            if (requiredType.isInstance(entry.getValue())) {
                matchingNames.add(entry.getKey());
            }
        }
        
        if (matchingNames.isEmpty()) {
            throw new NoSuchBeanException("No bean found of type: " + requiredType);
        }
        
        if (matchingNames.size() > 1) {
            throw new NoUniqueBeanException(
                "Multiple beans found of type: " + requiredType + ", names: " + matchingNames);
        }
        
        return (T) getBean(matchingNames.get(0));
    }
    
    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) {
        Map<String, T> result = new HashMap<>();
        
        // 从定义中查找
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitions.entrySet()) {
            if (type.isAssignableFrom(entry.getValue().getBeanClass())) {
                Object bean = getBean(entry.getKey());
                if (type.isInstance(bean)) {
                    result.put(entry.getKey(), type.cast(bean));
                }
            }
        }
        
        // 从单例缓存中查找
        for (Map.Entry<String, Object> entry : singletonBeans.entrySet()) {
            if (type.isInstance(entry.getValue())) {
                result.put(entry.getKey(), type.cast(entry.getValue()));
            }
        }
        
        return result;
    }
    
    @Override
    public String[] getBeanNamesForType(Class<?> type) {
        Set<String> names = new HashSet<>();
        
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitions.entrySet()) {
            if (type.isAssignableFrom(entry.getValue().getBeanClass())) {
                names.add(entry.getKey());
            }
        }
        
        for (Map.Entry<String, Object> entry : singletonBeans.entrySet()) {
            if (type.isInstance(entry.getValue())) {
                names.add(entry.getKey());
            }
        }
        
        return names.toArray(new String[0]);
    }
    
    @Override
    public Class<?> getType(String name) {
        BeanDefinition definition = beanDefinitions.get(name);
        if (definition != null) {
            return definition.getBeanClass();
        }
        
        Object bean = singletonBeans.get(name);
        if (bean != null) {
            return bean.getClass();
        }
        
        throw new NoSuchBeanException("No bean found with name: " + name);
    }
    
    @Override
    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
        beanDefinitions.put(name, beanDefinition);
    }
    
    @Override
    public void registerSingleton(String name, Object singletonObject) {
        singletonBeans.put(name, singletonObject);
    }
    
    @Override
    public void registerBeanFactory(String name, ObjectFactory<?> factory) {
        beanFactories.put(name, factory);
    }
    
    /**
     * 创建Bean实例
     */
    private Object createBean(String name, BeanDefinition definition) {
        // 检查作用域
        if (definition.getScope() == Scope.SINGLETON) {
            synchronized (singletonBeans) {
                Object cached = singletonBeans.get(name);
                if (cached != null) {
                    return cached;
                }
                
                Object bean = instantiateBean(definition);
                singletonBeans.put(name, bean);
                return bean;
            }
        } else {
            // 原型作用域，每次创建新实�?            return instantiateBean(definition);
        }
    }
    
    /**
     * 实例化Bean
     */
    private Object instantiateBean(BeanDefinition definition) {
        try {
            Class<?> beanClass = definition.getBeanClass();
            return beanClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new BeanCreationException("Failed to create bean: " + definition.getBeanClassName(), e);
        }
    }
}