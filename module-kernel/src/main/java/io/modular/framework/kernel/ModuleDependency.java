package io.modular.framework.kernel;

import java.util.Objects;

/**
 * 模块依赖项
 * 表示一个模块对另一个模块的依赖关系
 */
public class ModuleDependency {
    private final String moduleName;
    private final VersionRange versionRange;
    private final boolean optional;
    private final boolean transitive;
    
    private ModuleDependency(String moduleName, VersionRange versionRange, 
                            boolean optional, boolean transitive) {
        this.moduleName = moduleName;
        this.versionRange = versionRange;
        this.optional = optional;
        this.transitive = transitive;
    }
    
    /**
     * 解析依赖字符串
     * 格式：moduleName:versionRange[?][*]
     * 示例：
     *   "core.module:[1.0,2.0)"      // 必需依赖
     *   "util.module:1.5.0?"         // 可选依赖
     *   "common.module:[1.0,)*"      // 传递依赖
     */
    public static ModuleDependency parse(String dependencyStr) {
        if (dependencyStr == null || dependencyStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Dependency string cannot be null or empty");
        }
        
        String str = dependencyStr.trim();
        boolean optional = str.endsWith("?");
        boolean transitive = str.endsWith("*");
        
        if (optional) {
            str = str.substring(0, str.length() - 1);
        } else if (transitive) {
            str = str.substring(0, str.length() - 1);
        }
        
        // 分割模块名和版本范围
        int colonIndex = str.indexOf(':');
        if (colonIndex == -1) {
            throw new IllegalArgumentException("Invalid dependency format, missing ':': " + dependencyStr);
        }
        
        String moduleName = str.substring(0, colonIndex).trim();
        String versionRangeStr = str.substring(colonIndex + 1).trim();
        
        VersionRange versionRange = VersionRange.parse(versionRangeStr);
        
        return new ModuleDependency(moduleName, versionRange, optional, transitive);
    }
    
    /**
     * 创建必需依赖
     */
    public static ModuleDependency required(String moduleName, String versionRange) {
        return new ModuleDependency(moduleName, VersionRange.parse(versionRange), false, true);
    }
    
    /**
     * 创建必需依赖
     */
    public static ModuleDependency required(String moduleName, VersionRange versionRange) {
        return new ModuleDependency(moduleName, versionRange, false, true);
    }
    
    /**
     * 创建可选依赖
     */
    public static ModuleDependency optional(String moduleName, String versionRange) {
        return new ModuleDependency(moduleName, VersionRange.parse(versionRange), true, false);
    }
    
    /**
     * 创建可选依赖
     */
    public static ModuleDependency optional(String moduleName, VersionRange versionRange) {
        return new ModuleDependency(moduleName, versionRange, true, false);
    }
    
    /**
     * 创建非传递依赖
     */
    public static ModuleDependency nonTransitive(String moduleName, String versionRange) {
        return new ModuleDependency(moduleName, VersionRange.parse(versionRange), false, false);
    }
    
    /**
     * 创建非传递依赖
     */
    public static ModuleDependency nonTransitive(String moduleName, VersionRange versionRange) {
        return new ModuleDependency(moduleName, versionRange, false, false);
    }
    
    public String getModuleName() {
        return moduleName;
    }
    
    public VersionRange getVersionRange() {
        return versionRange;
    }
    
    public boolean isOptional() {
        return optional;
    }
    
    public boolean isTransitive() {
        return transitive;
    }
    
    /**
     * 检查指定模块是否满足此依赖
     */
    public boolean isSatisfiedBy(Module module) {
        if (!module.getName().equals(moduleName)) {
            return false;
        }
        
        return versionRange.contains(module.getId().getVersion());
    }
    
    /**
     * 检查指定版本是否满足此依赖
     */
    public boolean isSatisfiedBy(Version version) {
        return versionRange.contains(version);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModuleDependency that = (ModuleDependency) o;
        return optional == that.optional &&
               transitive == that.transitive &&
               Objects.equals(moduleName, that.moduleName) &&
               Objects.equals(versionRange, that.versionRange);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(moduleName, versionRange, optional, transitive);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(moduleName).append(':').append(versionRange);
        if (optional) {
            sb.append('?');
        }
        if (transitive) {
            sb.append('*');
        }
        return sb.toString();
    }
}