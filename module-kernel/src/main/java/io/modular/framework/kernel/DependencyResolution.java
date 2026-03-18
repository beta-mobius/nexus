package io.modular.framework.kernel;

import java.util.Collections;
import java.util.List;

/**
 * 依赖解析结果
 */
public class DependencyResolution {
    private final boolean successful;
    private final String errorMessage;
    private final List<ModuleId> resolvedDependencies;
    
    private DependencyResolution(boolean successful, String errorMessage, 
                                List<ModuleId> resolvedDependencies) {
        this.successful = successful;
        this.errorMessage = errorMessage;
        this.resolvedDependencies = resolvedDependencies != null ? 
            Collections.unmodifiableList(resolvedDependencies) : 
            Collections.emptyList();
    }
    
    /**
     * 创建成功的解析结果
     */
    public static DependencyResolution success(List<ModuleId> resolvedDependencies) {
        return new DependencyResolution(true, null, resolvedDependencies);
    }
    
    /**
     * 创建失败的解析结果
     */
    public static DependencyResolution failure(String errorMessage) {
        return new DependencyResolution(false, errorMessage, null);
    }
    
    public boolean isSuccessful() {
        return successful;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public List<ModuleId> getResolvedDependencies() {
        return resolvedDependencies;
    }
}