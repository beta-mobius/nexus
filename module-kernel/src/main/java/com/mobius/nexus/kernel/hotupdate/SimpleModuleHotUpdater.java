package com.mobius.nexus.kernel.hotupdate;

import com.mobius.nexus.kernel.*;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 简单的模块热更新器实现
 * 支持基本的立即更新和优雅更新
 */
public class SimpleModuleHotUpdater implements ModuleHotUpdater {
    
    private final ModuleLoader moduleLoader;
    private final ModuleRegistry moduleRegistry;
    private final ModuleRepository moduleRepository;
    
    private final ConcurrentMap<ModuleId, HotUpdateStatus> updateStatuses = new ConcurrentHashMap<>();
    private final List<HotUpdateListener> listeners = new CopyOnWriteArrayList<>();
    
    public SimpleModuleHotUpdater(ModuleLoader moduleLoader, ModuleRegistry moduleRegistry, 
                                 ModuleRepository moduleRepository) {
        this.moduleLoader = Objects.requireNonNull(moduleLoader);
        this.moduleRegistry = Objects.requireNonNull(moduleRegistry);
        this.moduleRepository = Objects.requireNonNull(moduleRepository);
    }
    
    @Override
    public HotUpdateResult hotUpdate(Path modulePath) {
        return hotUpgrade(modulePath, UpdateStrategy.IMMEDIATE);
    }
    
    @Override
    public HotUpgradeResult hotUpgrade(Path modulePath, UpdateStrategy strategy) {
        try {
            // 1. 加载新模块
            Module newModule = moduleLoader.loadModule(new FileModuleLocation(modulePath));
            ModuleId newModuleId = newModule.getId();
            String moduleName = newModule.getName();
            
            // 2. 查找现有模块
            Optional<Module> existingModuleOpt = moduleRegistry.getModule(moduleName);
            
            if (!existingModuleOpt.isPresent()) {
                // 新模块，直接启动
                newModule.start();
                return HotUpgradeResult.success(newModuleId, "New module installed successfully");
            }
            
            Module existingModule = existingModuleOpt.get();
            ModuleId existingModuleId = existingModule.getId();
            
            // 3. 检查版本是否更新
            if (!isNewerVersion(newModuleId, existingModuleId)) {
                return HotUpgradeResult.failed(newModuleId, 
                    "New version " + newModuleId.getVersion() + 
                    " is not newer than existing version " + existingModuleId.getVersion(),
                    null);
            }
            
            // 4. 根据策略执行更新
            switch (strategy) {
                case IMMEDIATE:
                    return executeImmediateUpdate(existingModule, newModule);
                case GRACEFUL:
                    return executeGracefulUpdate(existingModule, newModule);
                default:
                    return HotUpgradeResult.failed(newModuleId, 
                        "Strategy " + strategy + " not yet implemented", null);
            }
            
        } catch (ModuleException e) {
            return HotUpgradeResult.failed(null, "Failed to load module: " + e.getMessage(), e);
        } catch (Exception e) {
            return HotUpgradeResult.failed(null, "Unexpected error during hot upgrade: " + e.getMessage(), e);
        }
    }
    
    @Override
    public HotUninstallResult uninstall(ModuleId moduleId, boolean graceful) {
        try {
            Optional<Module> moduleOpt = moduleRegistry.getModule(moduleId);
            if (!moduleOpt.isPresent()) {
                return HotUninstallResult.failed(moduleId, "Module not found", null);
            }
            
            Module module = moduleOpt.get();
            
            // 检查依赖关系
            if (hasDependentModules(module)) {
                return HotUninstallResult.failed(moduleId, 
                    "Module has dependent modules, cannot uninstall", null);
            }
            
            // 停止模块
            if (graceful) {
                // 优雅停止：等待正在处理的请求完成
                // 简单实现：先标记为停止中，然后停止
                notifyListeners(HotUpdateEvent.stopping(moduleId));
                Thread.sleep(1000); // 简单的等待
            }
            
            module.stop();
            moduleRegistry.unregister(moduleId);
            
            return HotUninstallResult.success(moduleId, "Module uninstalled successfully");
            
        } catch (Exception e) {
            return HotUninstallResult.failed(moduleId, "Failed to uninstall module: " + e.getMessage(), e);
        }
    }
    
    @Override
    public HotUpdateStatus getStatus(ModuleId moduleId) {
        return updateStatuses.get(moduleId);
    }
    
    @Override
    public List<ModuleHotUpdateStatus> getAllUpdateStatuses() {
        List<ModuleHotUpdateStatus> statuses = new ArrayList<>();
        for (HotUpdateStatus status : updateStatuses.values()) {
            statuses.add(new ModuleHotUpdateStatus(status.getModuleId(), status.getPhase(), 
                status.getCurrentOperation(), status.getProgress()));
        }
        return Collections.unmodifiableList(statuses);
    }
    
    @Override
    public boolean cancelUpdate(ModuleId moduleId) {
        HotUpdateStatus status = updateStatuses.get(moduleId);
        if (status != null && status.isInProgress()) {
            updateStatuses.put(moduleId, status.update(HotUpdateStatus.Phase.FAILED, 
                "Update cancelled by user", 0));
            notifyListeners(HotUpdateEvent.cancelled(moduleId));
            return true;
        }
        return false;
    }
    
    @Override
    public RollbackResult rollback(ModuleId moduleId) {
        // 简化实现：重新启动旧版本（需要保存旧版本）
        // 这里只是框架，实际需要版本管理
        return RollbackResult.failed(moduleId, "Rollback not yet implemented");
    }
    
    // 私有方法
    
    private HotUpgradeResult executeImmediateUpdate(Module oldModule, Module newModule) {
        ModuleId oldModuleId = oldModule.getId();
        ModuleId newModuleId = newModule.getId();
        
        try {
            // 开始更新状态
            HotUpdateStatus status = HotUpdateStatus.initial(oldModuleId, oldModuleId);
            updateStatuses.put(oldModuleId, status);
            notifyListeners(HotUpdateEvent.started(oldModuleId, newModuleId));
            
            // 停止旧模块
            oldModule.stop();
            
            // 启动新模块
            newModule.start();
            
            // 从注册表中移除旧模块，添加新模块
            moduleRegistry.unregister(oldModuleId);
            moduleRegistry.register(newModule);
            
            // 更新状态
            status = status.update(HotUpdateStatus.Phase.COMPLETED, "Update completed", 100, newModuleId);
            updateStatuses.put(oldModuleId, status);
            notifyListeners(HotUpdateEvent.completed(oldModuleId, newModuleId));
            
            return HotUpgradeResult.success(newModuleId, "Module updated successfully");
            
        } catch (Exception e) {
            updateStatuses.put(oldModuleId, 
                HotUpdateStatus.initial(oldModuleId, oldModuleId)
                    .update(HotUpdateStatus.Phase.FAILED, "Update failed: " + e.getMessage(), 0));
            notifyListeners(HotUpdateEvent.failed(oldModuleId, newModuleId, e));
            
            return HotUpgradeResult.failed(newModuleId, "Immediate update failed: " + e.getMessage(), e);
        }
    }
    
    private HotUpgradeResult executeGracefulUpdate(Module oldModule, Module newModule) {
        ModuleId oldModuleId = oldModule.getId();
        ModuleId newModuleId = newModule.getId();
        
        try {
            // 开始更新状态
            HotUpdateStatus status = HotUpdateStatus.initial(oldModuleId, oldModuleId)
                .update(HotUpdateStatus.Phase.LOADING_NEW_VERSION, "Loading new version", 20, newModuleId);
            updateStatuses.put(oldModuleId, status);
            notifyListeners(HotUpdateEvent.started(oldModuleId, newModuleId));
            
            // 1. 启动新模块（与旧模块并行运行）
            newModule.start();
            
            status = status.update(HotUpdateStatus.Phase.STARTING_NEW_VERSION, 
                "New version started, migrating services", 50);
            updateStatuses.put(oldModuleId, status);
            
            // 2. 迁移服务（简化：新服务注册，旧服务停止接收新请求）
            // 在实际实现中，这里应该有服务路由逻辑
            migrateServices(oldModule, newModule);
            
            status = status.update(HotUpdateStatus.Phase.MIGRATING_TRAFFIC, 
                "Services migrated, stopping old version", 80);
            updateStatuses.put(oldModuleId, status);
            
            // 3. 等待一段时间让旧请求完成
            Thread.sleep(2000);
            
            // 4. 停止旧模块
            oldModule.stop();
            
            // 5. 更新注册表
            moduleRegistry.unregister(oldModuleId);
            moduleRegistry.register(newModule);
            
            status = status.update(HotUpdateStatus.Phase.COMPLETED, 
                "Graceful update completed", 100, newModuleId);
            updateStatuses.put(oldModuleId, status);
            notifyListeners(HotUpdateEvent.completed(oldModuleId, newModuleId));
            
            return HotUpgradeResult.success(newModuleId, "Graceful update completed successfully");
            
        } catch (Exception e) {
            updateStatuses.put(oldModuleId, 
                HotUpdateStatus.initial(oldModuleId, oldModuleId)
                    .update(HotUpdateStatus.Phase.FAILED, "Graceful update failed: " + e.getMessage(), 0));
            notifyListeners(HotUpdateEvent.failed(oldModuleId, newModuleId, e));
            
            // 尝试回滚：停止新模块，重新启动旧模块
            try {
                newModule.stop();
                oldModule.start();
            } catch (Exception rollbackEx) {
                // 回滚失败
            }
            
            return HotUpgradeResult.failed(newModuleId, "Graceful update failed: " + e.getMessage(), e);
        }
    }
    
    private void migrateServices(Module oldModule, Module newModule) {
        // 简化实现：在实际系统中，这里应该有服务发现和路由逻辑
        // 例如：标记旧服务为"正在停止"，新服务为"活动"
        // 对于依赖这些服务的模块，应该更新它们的服务引用
    }
    
    private boolean hasDependentModules(Module module) {
        String moduleName = module.getName();
        // 检查是否有其他模块依赖此模块
        for (Module other : moduleRegistry.getAllModules()) {
            if (!other.getId().equals(module.getId())) {
                Map<String, String> deps = other.getDependencies();
                if (deps.containsKey(moduleName)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean isNewerVersion(ModuleId newVersion, ModuleId oldVersion) {
        // 简化：比较版本号
        try {
            Version newVer = Version.parse(newVersion.getVersion());
            Version oldVer = Version.parse(oldVersion.getVersion());
            return newVer.compareTo(oldVer) > 0;
        } catch (Exception e) {
            // 如果版本解析失败，保守处理：不认为是新版本
            return false;
        }
    }
    
    private void notifyListeners(HotUpdateEvent event) {
        for (HotUpdateListener listener : listeners) {
            try {
                listener.onHotUpdateEvent(event);
            } catch (Exception e) {
                // 忽略监听器异常
            }
        }
    }
    
    /**
     * 添加热更新监听器
     */
    public void addListener(HotUpdateListener listener) {
        listeners.add(Objects.requireNonNull(listener));
    }
    
    /**
     * 移除热更新监听器
     */
    public void removeListener(HotUpdateListener listener) {
        listeners.remove(listener);
    }
}
