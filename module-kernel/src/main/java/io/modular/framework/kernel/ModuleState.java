package io.modular.framework.kernel;

/**
 * 模块状态枚举
 * 表示模块在运行时的不同状态
 */
public enum ModuleState {
    /**
     * 已安装但未解析依赖
     */
    INSTALLED,
    
    /**
     * 依赖已解析，准备启动
     */
    RESOLVED,
    
    /**
     * 正在启动
     */
    STARTING,
    
    /**
     * 运行中
     */
    ACTIVE,
    
    /**
     * 正在停止
     */
    STOPPING,
    
    /**
     * 已卸载
     */
    UNINSTALLED
}