package io.modular.framework.api;

/**
 * 通知者接口
 */
public interface Advisor {
    
    /**
     * 获取通知
     */
    Advice getAdvice();
    
    /**
     * 获取切入点
     */
    Pointcut getPointcut();
}