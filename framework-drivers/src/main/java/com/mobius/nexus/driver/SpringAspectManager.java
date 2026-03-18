package com.mobius.nexus.driver;

import com.mobius.nexus.api.*;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.framework.ProxyCreator;
import org.springframework.aop.framework.ProxyFactory;

import java.lang.annotation.Annotation;

/**
 * Spring切面管理器适配�? */
public class SpringAspectManager implements AspectManager {
    
    @Override
    public void registerAspect(Object aspect) {
        // Spring AOP通过配置或注解注册切�?        throw new UnsupportedOperationException(
            "Dynamic aspect registration not supported in this adapter");
    }
    
    @Override
    public void registerAdvisor(Advisor advisor) {
        throw new UnsupportedOperationException(
            "Dynamic advisor registration not supported in this adapter");
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T createProxy(T target) {
        ProxyFactory factory = new ProxyFactory(target);
        factory.setProxyTargetClass(true);
        return (T) factory.getProxy();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T createProxy(T target, Class<?>... interfaces) {
        ProxyFactory factory = new ProxyFactory(target);
        for (Class<?> iface : interfaces) {
            factory.addInterface(iface);
        }
        factory.setProxyTargetClass(true);
        return (T) factory.getProxy();
    }
    
    @Override
    public Pointcut createPointcut(String expression) {
        // 简化实现，实际需要解析AspectJ表达�?        throw new UnsupportedOperationException(
            "Expression-based pointcut not supported in this adapter");
    }
    
    @Override
    public Pointcut createPointcut(Class<? extends Annotation> annotation) {
        // 基于注解的切入点
        return new AnnotationPointcut(annotation);
    }
    
    private static class AnnotationPointcut implements Pointcut {
        private final Class<? extends Annotation> annotation;
        
        AnnotationPointcut(Class<? extends Annotation> annotation) {
            this.annotation = annotation;
        }
        
        @Override
        public boolean matches(java.lang.reflect.Method method, Class<?> targetClass) {
            return method.isAnnotationPresent(annotation) || 
                   targetClass.isAnnotationPresent(annotation);
        }
        
        @Override
        public ClassFilter getClassFilter() {
            return clazz -> clazz.isAnnotationPresent(annotation);
        }
        
        @Override
        public MethodMatcher getMethodMatcher() {
            return new MethodMatcher() {
                @Override
                public boolean matches(java.lang.reflect.Method method, Class<?> targetClass) {
                    return method.isAnnotationPresent(annotation);
                }
                
                @Override
                public boolean isRuntime() {
                    return false;
                }
                
                @Override
                public boolean matches(java.lang.reflect.Method method, 
                                      Class<?> targetClass, Object... args) {
                    return matches(method, targetClass);
                }
            };
        }
    }
}