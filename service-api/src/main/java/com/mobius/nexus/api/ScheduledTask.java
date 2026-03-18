package com.mobius.nexus.api;

/**
 * 调度任务接口
 */
public interface ScheduledTask {
    
    /**
     * 取消任务
     * @param mayInterruptIfRunning 是否中断正在运行的任�?     * @return 是否成功取消
     */
    boolean cancel(boolean mayInterruptIfRunning);
    
    /**
     * 是否已取�?     */
    boolean isCancelled();
    
    /**
     * 是否已完�?     */
    boolean isDone();
}