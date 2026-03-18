package io.modular.framework.api;

import java.lang.reflect.Method;

/**
 * 方法匹配器接口
 */
public interface MethodMatcher {
    
    /**
     * 静态匹配（不考虑参数）
     * @param method 方法
     * @param targetClass 目标类
     * @return 是否匹配
     */
    boolean matches(Method method, Class<?> targetClass);
    
    /**
     * 是否为运行时匹配（需要考虑参数）
     */
    boolean isRuntime();
    
    /**
     * 运行时匹配（考虑参数）
     * @param method 方法
     * @param targetClass 目标类
     * @param args 参数
     * @return 是否匹配
     */
    boolean matches(Method method, Class<?> targetClass, Object... args);
}