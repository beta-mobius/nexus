package io.modular.framework.api;

/**
 * 约束违规接口
 * @param <T> 目标类型
 */
public interface ConstraintViolation<T> {
    
    /**
     * 获取违规消息
     */
    String getMessage();
    
    /**
     * 获取违规属性路径
     */
    String getPropertyPath();
    
    /**
     * 获取违规值
     */
    Object getInvalidValue();
    
    /**
     * 获取根Bean类型
     */
    Class<T> getRootBeanClass();
    
    /**
     * 获取根Bean实例
     */
    T getRootBean();
}