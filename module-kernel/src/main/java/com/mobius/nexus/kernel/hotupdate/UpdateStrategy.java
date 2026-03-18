package com.mobius.nexus.kernel.hotupdate;

/**
 * 更新策略枚举
 */
public enum UpdateStrategy {
    
    /**
     * 立即更新 - 停止旧版本，立即启动新版本
     * 优点：简单快速
     * 缺点：有短暂的服务中断
     */
    IMMEDIATE,
    
    /**
     * 蓝绿部署 - 并行运行新旧版本，完全切换
     * 优点：零停机时间
     * 缺点：需要双倍资源
     */
    BLUE_GREEN,
    
    /**
     * 金丝雀发布 - 逐步将流量从旧版本迁移到新版本
     * 优点：风险可控
     * 缺点：实现复杂
     */
    CANARY,
    
    /**
     * 滚动更新 - 逐个替换实例
     * 优点：服务不中断
     * 缺点：版本混合运行
     */
    ROLLING,
    
    /**
     * 优雅更新 - 等待当前请求完成后再切换
     * 优点：请求不中断
     * 缺点：更新时间较长
     */
    GRACEFUL,
    
    /**
     * 并行运行 - 新旧版本同时运行，新请求路由到新版本
     * 优点：无中断，旧请求可继续完成
     * 缺点：资源消耗大，需要请求路由
     */
    PARALLEL;
    
    /**
     * 判断策略是否需要并行运行新旧版本
     */
    public boolean requiresParallelExecution() {
        return this == BLUE_GREEN || this == CANARY || this == PARALLEL;
    }
    
    /**
     * 判断策略是否允许逐步迁移
     */
    public boolean allowsGradualMigration() {
        return this == CANARY || this == ROLLING || this == GRACEFUL;
    }
    
    /**
     * 判断策略是否支持零停机
     */
    public boolean supportsZeroDowntime() {
        return this != IMMEDIATE;
    }
}
