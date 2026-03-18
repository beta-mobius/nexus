package com.mobius.nexus.api;

import java.time.Instant;

/**
 * 触发器上下文
 */
public interface TriggerContext {
    
    /**
     * 获取上次调度执行时间
     */
    Instant lastScheduledExecutionTime();
    
    /**
     * 获取上次实际执行时间
     */
    Instant lastActualExecutionTime();
    
    /**
     * 获取上次完成时间
     */
    Instant lastCompletionTime();
}