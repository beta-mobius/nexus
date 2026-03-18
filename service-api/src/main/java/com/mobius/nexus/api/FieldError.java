package com.mobius.nexus.api;

/**
 * 字段错误
 */
public class FieldError extends ObjectError {
    
    private final String field;
    private final Object rejectedValue;
    private final boolean bindingFailure;
    
    public FieldError(String objectName, String field, Object rejectedValue, 
                     boolean bindingFailure, String code, String defaultMessage) {
        super(objectName, code, defaultMessage);
        this.field = field;
        this.rejectedValue = rejectedValue;
        this.bindingFailure = bindingFailure;
    }
    
    public String getField() {
        return field;
    }
    
    public Object getRejectedValue() {
        return rejectedValue;
    }
    
    public boolean isBindingFailure() {
        return bindingFailure;
    }
}