package com.mobius.nexus.kernel;

import java.util.Objects;

/**
 * 模块标识符
 * 唯一标识一个模块，包含符号名称和版本
 */
public final class ModuleId {
    private final String symbolicName;
    private final Version version;
    
    /**
     * 私有构造函数
     */
    private ModuleId(String symbolicName, Version version) {
        if (symbolicName == null || symbolicName.trim().isEmpty()) {
            throw new IllegalArgumentException("Symbolic name cannot be null or empty");
        }
        if (version == null) {
            throw new IllegalArgumentException("Version cannot be null");
        }
        this.symbolicName = symbolicName.trim();
        this.version = version;
    }
    
    /**
     * 创建模块标识符
     * @param name 符号名称
     * @param version 版本字符串
     * @return ModuleId对象
     */
    public static ModuleId of(String name, String version) {
        return new ModuleId(name, Version.parse(version));
    }
    
    /**
     * 创建模块标识符
     * @param name 符号名称
     * @param version Version对象
     * @return ModuleId对象
     */
    public static ModuleId of(String name, Version version) {
        return new ModuleId(name, version);
    }
    
    /**
     * 获取符号名称
     */
    public String getSymbolicName() {
        return symbolicName;
    }
    
    /**
     * 获取版本
     */
    public Version getVersion() {
        return version;
    }
    
    /**
     * 获取名称（符号名称）
     */
    public String getName() {
        return symbolicName;
    }
    
    /**
     * 检查是否与另一个模块ID兼容（相同名称，主版本兼容）
     */
    public boolean isCompatibleWith(ModuleId other) {
        return this.symbolicName.equals(other.symbolicName) &&
               this.version.isCompatibleWith(other.version);
    }
    
    /**
     * 检查是否是相同模块（名称相同）
     */
    public boolean isSameModule(ModuleId other) {
        return this.symbolicName.equals(other.symbolicName);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModuleId moduleId = (ModuleId) o;
        return symbolicName.equals(moduleId.symbolicName) &&
               version.equals(moduleId.version);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(symbolicName, version);
    }
    
    @Override
    public String toString() {
        return symbolicName + ":" + version;
    }
}