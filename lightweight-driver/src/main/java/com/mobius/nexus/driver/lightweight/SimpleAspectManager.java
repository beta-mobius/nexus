package com.mobius.nexus.driver.lightweight;

import com.mobius.nexus.api.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 简单切面管理器实现
 * 基于JDK动态代理的AOP支持
 */
public class SimpleAspectManager implements AspectManager {
    
    private final List<Object> aspects = new CopyOnWriteArrayList<>();
    private final List<Advisor> advisors = new CopyOnWriteArrayList<>();
    
    @Override
    public void registerAspect(Object aspect) {
        aspects.add(aspect);
        System.out.println("Aspect registered: " + aspect.getClass().getName());
    }
    
    @Override
    public void registerAdvisor(Advisor advisor) {
        advisors.add(advisor);
        System.out.println("Advisor registered: " + advisor);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T createProxy(T target) {
        Class<?> targetClass = target.getClass();
        Class<?>[] interfaces = targetClass.getInterfaces();
        
        if (interfaces.length == 0) {
            // 如果没有接口，使用CGLIB代理（简化：返回原对象）
            System.out.println("Warning: Target class has no interfaces, using original object");
            return target;
        }
        
        return (T) Proxy.newProxyInstance(
            targetClass.getClassLoader(),
            interfaces,
            new SimpleInvocationHandler(target)
        );
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T createProxy(T target, Class<?>... interfaces) {
        return (T) Proxy.newProxyInstance(
            target.getClass().getClassLoader(),
            interfaces,
            new SimpleInvocationHandler(target)
        );
    }
    
    @Override
    public Pointcut createPointcut(String expression) {
        // 简化实现，不支持表达式
        throw new UnsupportedOperationException("Expression-based pointcuts not supported in lightweight driver");
    }
    
    @Override
    public Pointcut createPointcut(Class<? extends Annotation> annotation) {
        return new AnnotationPointcut(annotation);
    }
    
    /**
     * 简单的调用处理�?     */
    private class SimpleInvocationHandler implements InvocationHandler {
        private final Object target;
        
        SimpleInvocationHandler(Object target) {
            this.target = target;
        }
        
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 前置通知
            for (Object aspect : aspects) {
                invokeBeforeAdvice(aspect, method, args, target);
            }
            
            for (Advisor advisor : advisors) {
                Advice advice = advisor.getAdvice();
                if (advice instanceof io.modular.framework.api.BeforeAdvice) {
                    Pointcut pointcut = advisor.getPointcut();
                    if (pointcut.matches(method, target.getClass())) {
                        ((io.modular.framework.api.BeforeAdvice) advice).before(method, args, target);
                    }
                }
            }
            
            try {
                // 执行目标方法
                Object result = method.invoke(target, args);
                
                // 后置通知（成功时�?                for (Object aspect : aspects) {
                    invokeAfterReturningAdvice(aspect, method, args, target, result);
                }
                
                return result;
            } catch (Exception e) {
                // 异常通知
                for (Object aspect : aspects) {
                    invokeAfterThrowingAdvice(aspect, method, args, target, e);
                }
                throw e;
            } finally {
                // 最终通知
                for (Object aspect : aspects) {
                    invokeAfterAdvice(aspect, method, args, target);
                }
            }
        }
        
        private void invokeBeforeAdvice(Object aspect, Method method, Object[] args, Object target) {
            try {
                Method[] methods = aspect.getClass().getMethods();
                for (Method m : methods) {
                    if (m.getName().equals("before") && 
                        m.getParameterCount() == 3 &&
                        m.getParameterTypes()[0] == Method.class &&
                        m.getParameterTypes()[1] == Object[].class &&
                        m.getParameterTypes()[2] == Object.class) {
                        m.invoke(aspect, method, args, target);
                    }
                }
            } catch (Exception e) {
                // 忽略通知执行异常
            }
        }
        
        private void invokeAfterReturningAdvice(Object aspect, Method method, Object[] args, Object target, Object result) {
            try {
                Method[] methods = aspect.getClass().getMethods();
                for (Method m : methods) {
                    if (m.getName().equals("afterReturning") && 
                        m.getParameterCount() == 4 &&
                        m.getParameterTypes()[0] == Method.class &&
                        m.getParameterTypes()[1] == Object[].class &&
                        m.getParameterTypes()[2] == Object.class &&
                        m.getParameterTypes()[3] == Object.class) {
                        m.invoke(aspect, method, args, target, result);
                    }
                }
            } catch (Exception e) {
                // 忽略通知执行异常
            }
        }
        
        private void invokeAfterThrowingAdvice(Object aspect, Method method, Object[] args, Object target, Exception ex) {
            try {
                Method[] methods = aspect.getClass().getMethods();
                for (Method m : methods) {
                    if (m.getName().equals("afterThrowing") && 
                        m.getParameterCount() == 4 &&
                        m.getParameterTypes()[0] == Method.class &&
                        m.getParameterTypes()[1] == Object[].class &&
                        m.getParameterTypes()[2] == Object.class &&
                        m.getParameterTypes()[3] == Exception.class) {
                        m.invoke(aspect, method, args, target, ex);
                    }
                }
            } catch (Exception e) {
                // 忽略通知执行异常
            }
        }
        
        private void invokeAfterAdvice(Object aspect, Method method, Object[] args, Object target) {
            try {
                Method[] methods = aspect.getClass().getMethods();
                for (Method m : methods) {
                    if (m.getName().equals("after") && 
                        m.getParameterCount() == 3 &&
                        m.getParameterTypes()[0] == Method.class &&
                        m.getParameterTypes()[1] == Object[].class &&
                        m.getParameterTypes()[2] == Object.class) {
                        m.invoke(aspect, method, args, target);
                    }
                }
            } catch (Exception e) {
                // 忽略通知执行异常
            }
        }
    }
    
    /**
     * 基于注解的切入点
     */
    private static class AnnotationPointcut implements Pointcut {
        private final Class<? extends Annotation> annotation;
        
        AnnotationPointcut(Class<? extends Annotation> annotation) {
            this.annotation = annotation;
        }
        
        @Override
        public boolean matches(Method method, Class<?> targetClass) {
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
                public boolean matches(Method method, Class<?> targetClass) {
                    return method.isAnnotationPresent(annotation);
                }
                
                @Override
                public boolean isRuntime() {
                    return false;
                }
                
                @Override
                public boolean matches(Method method, Class<?> targetClass, Object... args) {
                    return matches(method, targetClass);
                }
            };
        }
    }
}