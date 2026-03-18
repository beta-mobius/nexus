package com.mobius.nexus.kernel.hotupdate;

import com.mobius.nexus.kernel.ModuleId;
import java.time.Instant;
import java.util.Objects;

/**
 * 回滚结果
 */
public class RollbackResult {
    
    public enum Status {
        /** 成功 */
        SUCCESS,
        /** 进行中 */
        IN_PROGRESS,
        /** 失败 */
        FAILED,
        /** 无需回滚 */
        NOT_NEEDED,
        /** 无法回滚（没有保存旧版本） */
        CANNOT_ROLLBACK;
    }
    
    private final ModuleId moduleId;
    private final Status status;
    private final String message;
    private final Instant timestamp;
    private final Throwable error;
    
    public RollbackResult(ModuleId moduleId, Status status, String message) {
        this(moduleId, status, message, Instant.now(), null);
    }
    
    public RollbackResult(ModuleId moduleId, Status status, String message, Throwable error) {
        this(moduleId, status, message, Instant.now(), error);
    }
    
    public RollbackResult(ModuleId moduleId, Status status, String message, 
                         Instant timestamp, Throwable error) {
        this.moduleId = moduleId;
        this.status = Objects.requireNonNull(status);
        this.message = Objects.requireNonNull(message);
        this.timestamp = Objects.requireNonNull(timestamp);
        this.error = error;
    }
    
    public ModuleId getModuleId() {
        return moduleId;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    public Throwable getError() {
        return error;
    }
    
    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }
    
    public boolean isFailed() {
        return status == Status.FAILED;
    }
    
    /**
     * 创建成功结果
     */
    public static RollbackResult success(ModuleId moduleId, String message) {
        return new RollbackResult(moduleId, Status.SUCCESS, message);
    }
    
    /**
     * 创建进行中结果
     */
    public static RollbackResult inProgress(ModuleId moduleId, String message) {
        return new RollbackResult(moduleId, Status.IN_PROGRESS, message);
    }
    
    /**
     * 创建失败结果
     */
    public static RollbackResult failed(ModuleId moduleId, String message) {
        return new RollbackResult(moduleId, Status.FAILED, message);
    }
    
    /**
     * 创建失败结果（带异常）
     */
    public static RollbackResult failed(ModuleId moduleId, String message, Throwable error) {
        return new RollbackResult(moduleId, Status.FAILED, message, error);
    }
    
    /**
     * 创建无需回滚结果
     */
    public static RollbackResult notNeeded(ModuleId moduleId, String message) {
        return new RollbackResult(moduleId, Status.NOT_NEEDED, message);
    }
    
    /**
     * 创建无法回滚结果
     */
    public static RollbackResult cannotRollback(ModuleId moduleId, String message) {
        return new RollbackResult(moduleId, Status.CANNOT_ROLLBACK, message);
    }
    
    @Override
    public String toString() {
        return String.format("RollbackResult{moduleId=%s, status=%s, message='%s', timestamp=%s}",
            moduleId, status, message, timestamp);
    }
}
