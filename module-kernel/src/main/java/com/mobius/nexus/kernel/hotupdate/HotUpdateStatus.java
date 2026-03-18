package com.mobius.nexus.kernel.hotupdate;

import com.mobius.nexus.kernel.ModuleId;
import com.mobius.nexus.kernel.Module;
import java.time.Instant;
import java.util.Objects;

/**
 * 模块热更新状态
 */
public class HotUpdateStatus {
    
    public enum Phase {
        /** 初始状态 */
        INITIAL,
        /** 检查依赖 */
        CHECKING_DEPENDENCIES,
        /** 加载新版本 */
        LOADING_NEW_VERSION,
        /** 启动新版本 */
        STARTING_NEW_VERSION,
        /** 迁移流量 */
        MIGRATING_TRAFFIC,
        /** 停止旧版本 */
        STOPPING_OLD_VERSION,
        /** 清理资源 */
        CLEANING_UP,
        /** 完成 */
        COMPLETED,
        /** 回滚中 */
        ROLLING_BACK,
        /** 已回滚 */
        ROLLED_BACK,
        /** 失败 */
        FAILED;
    }
    
    private final ModuleId moduleId;
    private final Phase phase;
    private final Instant startTime;
    private final Instant lastUpdateTime;
    private final String currentOperation;
    private final int progress; // 0-100
    private final ModuleId oldVersion;
    private final ModuleId newVersion;
    
    public HotUpdateStatus(ModuleId moduleId, Phase phase, String currentOperation, 
                          int progress, ModuleId oldVersion, ModuleId newVersion) {
        this.moduleId = Objects.requireNonNull(moduleId);
        this.phase = Objects.requireNonNull(phase);
        this.startTime = Instant.now();
        this.lastUpdateTime = Instant.now();
        this.currentOperation = Objects.requireNonNull(currentOperation);
        this.progress = validateProgress(progress);
        this.oldVersion = oldVersion;
        this.newVersion = newVersion;
    }
    
    public HotUpdateStatus(ModuleId moduleId, Phase phase, String currentOperation, 
                          int progress, ModuleId oldVersion, ModuleId newVersion,
                          Instant startTime, Instant lastUpdateTime) {
        this.moduleId = Objects.requireNonNull(moduleId);
        this.phase = Objects.requireNonNull(phase);
        this.startTime = Objects.requireNonNull(startTime);
        this.lastUpdateTime = Objects.requireNonNull(lastUpdateTime);
        this.currentOperation = Objects.requireNonNull(currentOperation);
        this.progress = validateProgress(progress);
        this.oldVersion = oldVersion;
        this.newVersion = newVersion;
    }
    
    private int validateProgress(int progress) {
        if (progress < 0 || progress > 100) {
            throw new IllegalArgumentException("Progress must be between 0 and 100: " + progress);
        }
        return progress;
    }
    
    public ModuleId getModuleId() {
        return moduleId;
    }
    
    public Phase getPhase() {
        return phase;
    }
    
    public Instant getStartTime() {
        return startTime;
    }
    
    public Instant getLastUpdateTime() {
        return lastUpdateTime;
    }
    
    public String getCurrentOperation() {
        return currentOperation;
    }
    
    public int getProgress() {
        return progress;
    }
    
    public ModuleId getOldVersion() {
        return oldVersion;
    }
    
    public ModuleId getNewVersion() {
        return newVersion;
    }
    
    public boolean isInProgress() {
        return phase != Phase.COMPLETED && phase != Phase.FAILED && phase != Phase.ROLLED_BACK;
    }
    
    public boolean isFailed() {
        return phase == Phase.FAILED;
    }
    
    public boolean isCompleted() {
        return phase == Phase.COMPLETED;
    }
    
    public boolean isRollingBack() {
        return phase == Phase.ROLLING_BACK;
    }
    
    /**
     * 创建新的状态更新
     */
    public HotUpdateStatus update(Phase newPhase, String newOperation, int newProgress) {
        return new HotUpdateStatus(
            moduleId, newPhase, newOperation, newProgress, oldVersion, newVersion,
            startTime, Instant.now()
        );
    }
    
    /**
     * 创建新的状态更新（带新版本信息）
     */
    public HotUpdateStatus update(Phase newPhase, String newOperation, int newProgress, 
                                 ModuleId newVersion) {
        return new HotUpdateStatus(
            moduleId, newPhase, newOperation, newProgress, oldVersion, newVersion,
            startTime, Instant.now()
        );
    }
    
    /**
     * 创建初始状态
     */
    public static HotUpdateStatus initial(ModuleId moduleId, ModuleId oldVersion) {
        return new HotUpdateStatus(
            moduleId, Phase.INITIAL, "Preparing for hot update", 0, oldVersion, null
        );
    }
    
    @Override
    public String toString() {
        return String.format("HotUpdateStatus{moduleId=%s, phase=%s, progress=%d%%, operation='%s'}",
            moduleId, phase, progress, currentOperation);
    }
}
