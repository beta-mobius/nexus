package com.mobius.nexus.kernel.hotupdate;

import com.mobius.nexus.kernel.ModuleId;
import java.util.Objects;

/**
 * 模块热更新状态（简版，用于外部查询）
 */
public class ModuleHotUpdateStatus {
    
    private final ModuleId moduleId;
    private final String phase;
    private final String currentOperation;
    private final int progress;
    
    public ModuleHotUpdateStatus(ModuleId moduleId, String phase, 
                               String currentOperation, int progress) {
        this.moduleId = Objects.requireNonNull(moduleId);
        this.phase = Objects.requireNonNull(phase);
        this.currentOperation = Objects.requireNonNull(currentOperation);
        this.progress = validateProgress(progress);
    }
    
    public ModuleHotUpdateStatus(HotUpdateStatus status) {
        this(status.getModuleId(), status.getPhase().name(), 
             status.getCurrentOperation(), status.getProgress());
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
    
    public String getPhase() {
        return phase;
    }
    
    public String getCurrentOperation() {
        return currentOperation;
    }
    
    public int getProgress() {
        return progress;
    }
    
    @Override
    public String toString() {
        return String.format("ModuleHotUpdateStatus{moduleId=%s, phase=%s, progress=%d%%, operation='%s'}",
            moduleId, phase, progress, currentOperation);
    }
}
