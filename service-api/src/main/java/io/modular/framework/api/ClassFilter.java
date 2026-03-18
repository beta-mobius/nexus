package io.modular.framework.api;

/**
 * 类过滤器接口
 */
@FunctionalInterface
public interface ClassFilter {
    
    /**
     * 检查类是否匹配
     * @param clazz 类
     * @return 是否匹配
     */
    boolean matches(Class<?> clazz);
}