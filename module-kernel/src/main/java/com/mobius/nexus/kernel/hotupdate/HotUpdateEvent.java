package com.mobius.nexus.kernel.hotupdate;

import com.mobius.nexus.kernel.ModuleId;
import java.time.Instant;
import java.util.Objects;

/**
 * 热更新事件
 */
public class HotUpdateEvent {
    
    public enum Type {
        /** 更新开始 */
        UPDATE_STARTED,
        /** 阶段变更 */
        PHASE_CHANGED,
        /** 进度更新 */
        PROGRESS_UPDATED,
        /** 更新完成 */
        UPDATE_COMPLETED,
        /** 更新失败 */
        UPDATE_FAILED,
        /** 更新取消 */
        UPDATE_CANCELLED,
        /** 回滚开始 */
        ROLLBACK_STARTED,
        /** 回滚完成 */
        ROLLBACK_COMPLETED,
        /** 模块停止 */
        MODULE_STOPPING,
        /** 模块启动 */
        MODULE_STARTING;
    }
    
    private final Type type;
    private final ModuleId moduleId;
    private final ModuleId newModuleId;
    private final String message;
    private final Instant timestamp;
    private final Throwable error;
    
    public HotUpdateEvent(Type type, ModuleId moduleId, ModuleId newModuleId, 
                         String message, Throwable error) {
        this.type = Objects.requireNonNull(type);
        this.moduleId = moduleId;
        this.newModuleId = newModuleId;
        this.message = Objects.requireNonNull(message);
        this.timestamp = Instant.now();
        this.error = error;
    }
    
    public HotUpdateEvent(Type type, ModuleId moduleId, String message) {
        this(type, moduleId, null, message, null);
    }
    
    public HotUpdateEvent(Type type, ModuleId moduleId, ModuleId newModuleId, String message) {
        this(type, moduleId, newModuleId, message, null);
    }
    
    public Type getType() {
        return type;
    }
    
    public ModuleId getModuleId() {
        return moduleId;
    }
    
    public ModuleId getNewModuleId() {
        return newModuleId;
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
    
    public boolean isError() {
        return error != null;
    }
    
    // 静态工厂方法
    
    public static HotUpdateEvent started(ModuleId oldModuleId, ModuleId newModuleId) {
        return new HotUpdateEvent(Type.UPDATE_STARTED, oldModuleId, newModuleId, 
            "Hot update started");
    }
    
    public static HotUpdateEvent completed(ModuleId oldModuleId, ModuleId newModuleId) {
        return new HotUpdateEvent(Type.UPDATE_COMPLETED, oldModuleId, newModuleId, 
            "Hot update completed successfully");
    }
    
    public static HotUpdateEvent failed(ModuleId oldModuleId, ModuleId newModuleId, Throwable error) {
        return new HotUpdateEvent(Type.UPDATE_FAILED, oldModuleId, newModuleId, 
            "Hot update failed: " + error.getMessage(), error);
    }
    
    public static HotUpdateEvent cancelled(ModuleId moduleId) {
        return new HotUpdateEvent(Type.UPDATE_CANCELLED, moduleId, 
            "Hot update cancelled");
    }
    
    public static HotUpdateEvent stopping(ModuleId moduleId) {
        return new HotUpdateEvent(Type.MODULE_STOPPING, moduleId, 
            "Module is stopping");
    }
    
    public static HotUpdateEvent starting(ModuleId moduleId) {
        return new HotUpdateEvent(Type.MODULE_STARTING, moduleId, 
            "Module is starting");
    }
    
    public static HotUpdateEvent phaseChanged(ModuleId moduleId, String phase) {
        return new HotUpdateEvent(Type.PHASE_CHANGED, moduleId, 
            "Phase changed to: " + phase);
    }
    
    public static HotUpdateEvent progressUpdated(ModuleId moduleId, int progress) {
        return new HotUpdateEvent(Type.PROGRESS_UPDATED, moduleId, 
            "Progress: " + progress + "%");
    }
    
    @Override
    public String toString() {
        return String.format("HotUpdateEvent{type=%s, moduleId=%s, message='%s', timestamp=%s}",
            type, moduleId, message, timestamp);
    }
}
