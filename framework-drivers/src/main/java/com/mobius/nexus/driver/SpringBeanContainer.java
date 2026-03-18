package com.mobius.nexus.driver;

import com.mobius.nexus.api.BeanContainer;
import com.mobius.nexus.api.BeanDefinition;
import com.mobius.nexus.api.ConstructorArgumentValues;
import com.mobius.nexus.api.PropertyValue;
import com.mobius.nexus.api.Scope;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Spring Bean容器适配�? */
public class SpringBeanContainer implements BeanContainer {
    
    private final ApplicationContext applicationContext;
    
    public SpringBeanContainer(ApplicationContext context) {
        this.applicationContext = context;
    }
    
    @Override
    public <T> void registerBean(Class<T> beanClass) {
        throw new UnsupportedOperationException(
            "Dynamic bean registration not supported in this adapter");
    }
    
    @Override
    public <T> void registerBean(Class<T> beanClass, String name) {
        throw new UnsupportedOperationException(
            "Dynamic bean registration not supported in this adapter");
    }
    
    @Override
    public <T> void registerBean(T instance, String name) {
        throw new UnsupportedOperationException(
            "Dynamic bean registration not supported in this adapter");
    }
    
    @Override
    public <T> T getBean(Class<T> beanClass) {
        return applicationContext.getBean(beanClass);
    }
    
    @Override
    public <T> T getBean(String name, Class<T> beanClass) {
        return applicationContext.getBean(name, beanClass);
    }
    
    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> beanClass) {
        return applicationContext.getBeansOfType(beanClass);
    }
    
    @Override
    public BeanDefinition getBeanDefinition(String name) {
        org.springframework.beans.factory.config.BeanDefinition springDef = 
            applicationContext.getBeanFactory().getBeanDefinition(name);
        return new SpringBeanDefinitionAdapter(springDef);
    }
    
    @Override
    public List<String> getBeanDefinitionNames() {
        return applicationContext.getBeanDefinitionNames().stream()
            .collect(Collectors.toList());
    }
    
    @Override
    public void registerScope(String scopeName, Scope scope) {
        applicationContext.getBeanFactory().registerScope(
            scopeName, new SpringScopeAdapter(scope));
    }
    
    @Override
    public void refresh() {
        throw new UnsupportedOperationException(
            "ApplicationContext refresh not supported after initialization");
    }
    
    @Override
    public void close() {
        if (applicationContext instanceof org.springframework.context.ConfigurableApplicationContext) {
            ((org.springframework.context.ConfigurableApplicationContext) applicationContext).close();
        }
    }
    
    private static class SpringBeanDefinitionAdapter implements BeanDefinition {
        private final org.springframework.beans.factory.config.BeanDefinition springBeanDefinition;
        
        SpringBeanDefinitionAdapter(org.springframework.beans.factory.config.BeanDefinition springBeanDefinition) {
            this.springBeanDefinition = springBeanDefinition;
        }
        
        @Override
        public String getName() {
            return springBeanDefinition.getBeanName();
        }
        
        @Override
        public Class<?> getBeanClass() {
            try {
                return Class.forName(springBeanDefinition.getBeanClassName());
            } catch (ClassNotFoundException e) {
                return null;
            }
        }
        
        @Override
        public String getScope() {
            return springBeanDefinition.getScope();
        }
        
        @Override
        public boolean isSingleton() {
            return springBeanDefinition.isSingleton();
        }
        
        @Override
        public boolean isLazyInit() {
            return springBeanDefinition.isLazyInit();
        }
        
        @Override
        public List<PropertyValue> getPropertyValues() {
            return List.of();
        }
        
        @Override
        public ConstructorArgumentValues getConstructorArgumentValues() {
            return new ConstructorArgumentValues();
        }
    }
    
    private static class SpringScopeAdapter implements org.springframework.beans.factory.config.Scope {
        private final Scope scope;
        
        SpringScopeAdapter(Scope scope) {
            this.scope = scope;
        }
        
        @Override
        public Object get(String name, org.springframework.beans.factory.ObjectFactory<?> objectFactory) {
            return scope.get(name, objectFactory::getObject);
        }
        
        @Override
        public Object remove(String name) {
            return scope.remove(name);
        }
        
        @Override
        public void registerDestructionCallback(String name, Runnable callback) {
            scope.registerDestructionCallback(name, callback);
        }
        
        @Override
        public void registerCustomScope(String scopeName, org.springframework.beans.factory.config.Scope scope) {
            // 不需要实�?        }
        
        @Override
        public String[] getConversationId() {
            return null;
        }
        
        @Override
        public Object resolveContextualObject(String key) {
            return null;
        }
    }
}