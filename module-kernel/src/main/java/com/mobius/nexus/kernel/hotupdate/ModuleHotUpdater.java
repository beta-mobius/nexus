package com.mobius.nexus.kernel.hotupdate;

import com.mobius.nexus.kernel.*;
import java.nio.file.Path;
import java.util.List;

/**
 * 模块热更新器
 * 支持模块的动态更新、升级和卸载
 */
public interface ModuleHotUpdater {
    
    /**
     * 热更新模块（替换现有模块）
     * @param modulePath 新模块JAR文件路径
     * @return 更新结果
     */
    HotUpdateResult hotUpdate(Path modulePath);
    
    /**
     * 热升级模块（保留旧版本，逐步迁移）
     * @param modulePath 新版本模块JAR文件路径
     * @param strategy 更新策略
     * @return 升级结果
     */
    HotUpgradeResult hotUpgrade(Path modulePath, UpdateStrategy strategy);
    
    /**
     * 卸载模块
     * @param moduleId 模块标识符
     * @param graceful 是否优雅卸载（等待正在处理的请求完成）
     * @return 卸载结果
     */
    HotUninstallResult uninstall(ModuleId moduleId, boolean graceful);
    
    /**
     * 获取模块的热更新状态
     * @param moduleId 模块标识符
     * @return 热更新状态
     */
    HotUpdateStatus getStatus(ModuleId moduleId);
    
    /**
     * 获取所有正在热更新的模块状态
     * @return 模块热更新状态列表
     */
    List<ModuleHotUpdateStatus> getAllUpdateStatuses();
    
    /**
     * 取消正在进行的热更新操作
     * @param moduleId 模块标识符
     * @return 是否取消成功
     */
    boolean cancelUpdate(ModuleId moduleId);
    
    /**
     * 回滚热更新
     * @param moduleId 模块标识符
     * @return 回滚结果
     */
    RollbackResult rollback(ModuleId moduleId);
}