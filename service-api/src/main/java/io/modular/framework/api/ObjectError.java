package io.modular.framework.api;

/**
 * 对象错误（全局错误）
 */
public class ObjectError {
    
    private final String objectName;
    private final String code;
    private final String defaultMessage;
    
    public ObjectError(String objectName, String code, String defaultMessage) {
        this.objectName = objectName;
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
    
    public String getObjectName() {
        return objectName;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDefaultMessage() {
        return defaultMessage;
    }
}