package io.modular.framework.api;

import java.util.List;

/**
 * 错误收集器接口
 */
public interface Errors {
    
    /**
     * 拒绝整个对象
     * @param errorCode 错误代码
     */
    void reject(String errorCode);
    
    /**
     * 拒绝整个对象（带默认消息）
     * @param errorCode 错误代码
     * @param defaultMessage 默认消息
     */
    void reject(String errorCode, String defaultMessage);
    
    /**
     * 拒绝字段
     * @param field 字段名
     * @param errorCode 错误代码
     */
    void rejectValue(String field, String errorCode);
    
    /**
     * 拒绝字段（带默认消息）
     * @param field 字段名
     * @param errorCode 错误代码
     * @param defaultMessage 默认消息
     */
    void rejectValue(String field, String errorCode, String defaultMessage);
    
    /**
     * 是否有错误
     */
    boolean hasErrors();
    
    /**
     * 指定字段是否有错误
     * @param field 字段名
     */
    boolean hasFieldErrors(String field);
    
    /**
     * 获取全局错误列表
     */
    List<ObjectError> getGlobalErrors();
    
    /**
     * 获取字段错误列表
     */
    List<FieldError> getFieldErrors();
    
    /**
     * 获取指定字段的错误
     * @param field 字段名
     * @return 字段错误
     */
    FieldError getFieldError(String field);
}