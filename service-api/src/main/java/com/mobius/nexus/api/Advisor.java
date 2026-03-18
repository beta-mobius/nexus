package com.mobius.nexus.api;

/**
 * 通知者接�? */
public interface Advisor {
    
    /**
     * 获取通知
     */
    Advice getAdvice();
    
    /**
     * 获取切入�?     */
    Pointcut getPointcut();
}