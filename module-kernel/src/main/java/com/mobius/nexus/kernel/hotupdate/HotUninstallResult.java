package com.mobius.nexus.kernel.hotupdate;

import com.mobius.nexus.kernel.ModuleId;
import java.time.Instant;
import java.util.Objects;

/**
 * 热卸载结果
 */
public class HotUninstallResult {
    
    public enum Status {
        /** 成功 */
        SUCCESS,
        /** 进行中 */
        IN_PROGRESS,
        /** 失败 */
        FAILED,
        /** 模块不存在 */
        NOT_FOUND,
        /** 有依赖模块，无法卸载 */
        HAS_DEPENDENTS;
    }
    
    private final ModuleId moduleId;
    private final Status status;
    private final String message;
    private final Instant timestamp;
    private final Throwable error;
    
    public HotUninstallResult(ModuleId moduleId, Status status, String message) {
        this(moduleId, status, message, Instant.now(), null);
    }
    
    public HotUninstallResult(ModuleId moduleId, Status status, String message, Throwable error) {
        this(moduleId, status, message, Instant.now(), error);
    }
    
    public HotUninstallResult(ModuleId moduleId, Status status, String message, Instant timestamp, Throwable error) {
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
    public static HotUninstallResult success(ModuleId moduleId, String message) {
        return new HotUninstallResult(moduleId, Status.SUCCESS, message);
    }
    
    /**
     * 创建进行中结果
     */
    public static HotUninstallResult inProgress(ModuleId moduleId, String message) {
        return new HotUninstallResult(moduleId, Status.IN_PROGRESS, message);
    }
    
    /**
     * 创建失败结果
     */
    public static HotUninstallResult failed(ModuleId moduleId, String message, Throwable error) {
        return new HotUninstallResult(moduleId, Status.FAILED, message, error);
    }
    
    /**
     * 创建模块不存在结果
     */
    public static HotUninstallResult notFound(ModuleId moduleId, String message) {
        return new HotUninstallResult(moduleId, Status.NOT_FOUND, message);
    }
    
    /**
     * 创建有依赖模块结果
     */
    public static HotUninstallResult hasDependents(ModuleId moduleId, String message) {
        return new HotUninstallResult(moduleId, Status.HAS_DEPENDENTS, message);
    }
    
    @Override
    public String toString() {
        return String.format("HotUninstallResult{moduleId=%s, status=%s, message='%s', timestamp=%s}",
            moduleId, status, message, timestamp);
    }
}
