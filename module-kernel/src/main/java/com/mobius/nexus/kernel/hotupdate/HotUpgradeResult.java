package com.mobius.nexus.kernel.hotupdate;

import com.mobius.nexus.kernel.ModuleId;
import java.time.Instant;
import java.util.Objects;

/**
 * 热升级结果
 */
public class HotUpgradeResult {
    
    public enum Status {
        /** 成功 */
        SUCCESS,
        /** 进行中 */
        IN_PROGRESS,
        /** 失败 */
        FAILED,
        /** 无需升级（已经是新版本） */
        NOT_NEEDED,
        /** 版本冲突 */
        VERSION_CONFLICT;
    }
    
    private final ModuleId moduleId;
    private final Status status;
    private final String message;
    private final Instant timestamp;
    private final Throwable error;
    
    public HotUpgradeResult(ModuleId moduleId, Status status, String message) {
        this(moduleId, status, message, Instant.now(), null);
    }
    
    public HotUpgradeResult(ModuleId moduleId, Status status, String message, Throwable error) {
        this(moduleId, status, message, Instant.now(), error);
    }
    
    public HotUpgradeResult(ModuleId moduleId, Status status, String message, Instant timestamp, Throwable error) {
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
    public static HotUpgradeResult success(ModuleId moduleId, String message) {
        return new HotUpgradeResult(moduleId, Status.SUCCESS, message);
    }
    
    /**
     * 创建进行中结果
     */
    public static HotUpgradeResult inProgress(ModuleId moduleId, String message) {
        return new HotUpgradeResult(moduleId, Status.IN_PROGRESS, message);
    }
    
    /**
     * 创建失败结果
     */
    public static HotUpgradeResult failed(ModuleId moduleId, String message, Throwable error) {
        return new HotUpgradeResult(moduleId, Status.FAILED, message, error);
    }
    
    /**
     * 创建无需升级结果
     */
    public static HotUpgradeResult notNeeded(ModuleId moduleId, String message) {
        return new HotUpgradeResult(moduleId, Status.NOT_NEEDED, message);
    }
    
    /**
     * 创建版本冲突结果
     */
    public static HotUpgradeResult versionConflict(ModuleId moduleId, String message) {
        return new HotUpgradeResult(moduleId, Status.VERSION_CONFLICT, message);
    }
    
    @Override
    public String toString() {
        return String.format("HotUpgradeResult{moduleId=%s, status=%s, message='%s', timestamp=%s}",
            moduleId, status, message, timestamp);
    }
}
