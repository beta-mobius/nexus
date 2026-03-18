package io.modular.framework.api;

import java.time.Duration;
import java.time.Instant;

/**
 * 调度器API
 * 替代Spring的TaskScheduler，提供统一的调度接口
 */
public interface Scheduler {
    
    /**
     * 调度任务（按触发器）
     * @param task 任务
     * @param trigger 触发器
     * @return 调度任务
     */
    ScheduledTask schedule(Runnable task, Trigger trigger);
    
    /**
     * 调度任务（指定开始时间）
     * @param task 任务
     * @param startTime 开始时间
     * @return 调度任务
     */
    ScheduledTask schedule(Runnable task, Instant startTime);
    
    /**
     * 按固定频率调度任务
     * @param task 任务
     * @param startTime 开始时间
     * @param period 周期
     * @return 调度任务
     */
    ScheduledTask scheduleAtFixedRate(Runnable task, Instant startTime, Duration period);
    
    /**
     * 按固定延迟调度任务
     * @param task 任务
     * @param startTime 开始时间
     * @param delay 延迟
     * @return 调度任务
     */
    ScheduledTask scheduleWithFixedDelay(Runnable task, Instant startTime, Duration delay);
    
    /**
     * 取消调度任务
     * @param task 调度任务
     */
    void cancel(ScheduledTask task);
    
    /**
     * 关闭调度器
     */
    void shutdown();
}