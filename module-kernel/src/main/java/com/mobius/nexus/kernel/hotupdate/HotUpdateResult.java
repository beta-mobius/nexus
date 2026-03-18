package com.mobius.nexus.kernel.hotupdate;

import com.mobius.nexus.kernel.ModuleId;
import java.time.Instant;
import java.util.Objects;

/**
 * 热更新结果
 */
public class HotUpdateResult {
    
    public enum Status {
        /** 成功 */
        SUCCESS,
        /** 进行中 */
        IN_PROGRESS,
        /** 失败 */
        FAILED,
        /** 已取消 */
        CANCELLED,
        /** 需要回滚 */
        NEEDS_ROLLBACK;
    }
    
    private final ModuleId moduleId;
    private final Status status;
    private final String message;
    private final Instant timestamp;
    private final Throwable error;
    
    public HotUpdateResult(ModuleId moduleId, Status status, String message) {
        this(moduleId, status, message, Instant.now(), null);
    }
    
    public HotUpdateResult(ModuleId moduleId, Status status, String message, Throwable error) {
        this(moduleId, status, message, Instant.now(), error);
    }
    
    public HotUpdateResult(ModuleId moduleId, Status status, String message, Instant timestamp, Throwable error) {
        this.moduleId = Objects.requireNonNull(moduleId);
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
    
    public boolean isInProgress() {
        return status == Status.IN_PROGRESS;
    }
    
    /**
     * 创建成功结果
     */
    public static HotUpdateResult success(ModuleId moduleId, String message) {
        return new HotUpdateResult(moduleId, Status.SUCCESS, message);
    }
    
    /**
     * 创建进行中结果
     */
    public static HotUpdateResult inProgress(ModuleId moduleId, String message) {
        return new HotUpdateResult(moduleId, Status.IN_PROGRESS, message);
    }
    
    /**
     * 创建失败结果
     */
    public static HotUpdateResult failed(ModuleId moduleId, String message, Throwable error) {
        return new HotUpdateResult(moduleId, Status.FAILED, message, error);
    }
    
    /**
     * 创建需要回滚结果
     */
    public static HotUpdateResult needsRollback(ModuleId moduleId, String message) {
        return new HotUpdateResult(moduleId, Status.NEEDS_ROLLBACK, message);
    }
    
    @Override
    public String toString() {
        return String.format("HotUpdateResult{moduleId=%s, status=%s, message='%s', timestamp=%s}",
            moduleId, status, message, timestamp);
    }
}
