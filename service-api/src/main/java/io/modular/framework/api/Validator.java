package io.modular.framework.api;

import java.util.Set;

/**
 * 验证器API
 * 替代Spring的Validator，提供统一的验证接口
 */
public interface Validator {
    
    /**
     * 是否支持验证指定类型
     * @param clazz 类型
     * @return 是否支持
     */
    boolean supports(Class<?> clazz);
    
    /**
     * 验证目标对象（使用Errors对象收集错误）
     * @param target 目标对象
     * @param errors 错误收集器
     */
    void validate(Object target, Errors errors);
    
    /**
     * 验证目标对象（返回约束违规集合）
     * @param target 目标对象
     * @return 约束违规集合
     */
    Set<ConstraintViolation<Object>> validate(Object target);
}