package io.modular.framework.api;

/**
 * 切入点接口
 */
public interface Pointcut {
    
    /**
     * 检查方法是否匹配切入点
     * @param method 方法
     * @param targetClass 目标类
     * @return 是否匹配
     */
    boolean matches(java.lang.reflect.Method method, Class<?> targetClass);
    
    /**
     * 获取类过滤器
     */
    ClassFilter getClassFilter();
    
    /**
     * 获取方法匹配器
     */
    MethodMatcher getMethodMatcher();
}