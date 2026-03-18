package io.modular.framework.kernel;

/**
 * 模块相关异常
 */
public class ModuleException extends Exception {
    
    public ModuleException(String message) {
        super(message);
    }
    
    public ModuleException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ModuleException(Throwable cause) {
        super(cause);
    }
}