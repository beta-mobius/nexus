package com.mobius.nexus.kernel;

import java.util.List;

/**
 * 模块仓库接口
 * 负责模块的存储、检索和版本管理
 */
public interface ModuleRepository {
    
    /**
     * 存储模块
     * @param moduleArchive 模块归档
     */
    void storeModule(ModuleArchive moduleArchive);
    
    /**
     * 检索模�?     * @param moduleId 模块标识
     * @return 模块归档
     */
    ModuleArchive retrieveModule(ModuleId moduleId);
    
    /**
     * 获取模块的可用版�?     * @param moduleName 模块名称
     * @return 版本列表
     */
    List<Version> getAvailableVersions(String moduleName);
    
    /**
     * 解析版本
     * @param moduleName 模块名称
     * @param versionRange 版本范围
     * @return 匹配的模块标�?     */
    ModuleId resolveVersion(String moduleName, VersionRange versionRange);
    
    /**
     * 解析依赖
     * @param descriptor 模块描述�?     * @return 依赖解析结果
     */
    DependencyResolution resolveDependencies(ModuleDescriptor descriptor);
}