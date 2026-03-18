package io.modular.framework.api;

/**
 * 调度任务接口
 */
public interface ScheduledTask {
    
    /**
     * 取消任务
     * @param mayInterruptIfRunning 是否中断正在运行的任务
     * @return 是否成功取消
     */
    boolean cancel(boolean mayInterruptIfRunning);
    
    /**
     * 是否已取消
     */
    boolean isCancelled();
    
    /**
     * 是否已完成
     */
    boolean isDone();
}