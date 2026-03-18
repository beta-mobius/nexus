package io.modular.framework.api;

/**
 * 切面管理器API
 * 替代Spring的AOP，提供统一的切面管理接口
 */
public interface AspectManager {
    
    /**
     * 注册切面实例
     * @param aspect 切面实例
     */
    void registerAspect(Object aspect);
    
    /**
     * 注册通知者
     * @param advisor 通知者
     */
    void registerAdvisor(Advisor advisor);
    
    /**
     * 创建代理
     * @param target 目标对象
     * @param <T> 目标类型
     * @return 代理对象
     */
    <T> T createProxy(T target);
    
    /**
     * 创建代理（指定接口）
     * @param target 目标对象
     * @param interfaces 代理实现的接口
     * @param <T> 目标类型
     * @return 代理对象
     */
    <T> T createProxy(T target, Class<?>... interfaces);
    
    /**
     * 创建切入点
     * @param expression 切入点表达式
     * @return 切入点
     */
    Pointcut createPointcut(String expression);
    
    /**
     * 创建切入点（基于注解）
     * @param annotation 注解类型
     * @return 切入点
     */
    Pointcut createPointcut(Class<? extends java.lang.annotation.Annotation> annotation);
}