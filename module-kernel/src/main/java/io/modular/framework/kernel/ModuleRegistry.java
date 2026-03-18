package io.modular.framework.kernel;

import java.util.Collection;
import java.util.Optional;

/**
 * 模块注册表
 * 管理所有已加载的模块
 */
public interface ModuleRegistry {
    
    /**
     * 注册模块
     * @param module 模块实例
     */
    void register(Module module);
    
    /**
     * 注销模块
     * @param moduleId 模块标识
     */
    void unregister(ModuleId moduleId);
    
    /**
     * 获取模块
     * @param moduleId 模块标识
     * @return 模块实例，如果不存在返回Optional.empty()
     */
    Optional<Module> getModule(ModuleId moduleId);
    
    /**
     * 根据名称获取模块（返回最新版本）
     * @param moduleName 模块名称
     * @return 模块实例，如果不存在返回Optional.empty()
     */
    Optional<Module> getModule(String moduleName);
    
    /**
     * 获取所有模块
     */
    Collection<Module> getAllModules();
    
    /**
     * 检查模块是否存在
     */
    boolean contains(ModuleId moduleId);
    
    /**
     * 获取模块数量
     */
    int size();
    
    /**
     * 清空所有模块
     */
    void clear();
}