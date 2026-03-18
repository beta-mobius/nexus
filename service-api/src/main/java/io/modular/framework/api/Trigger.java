package io.modular.framework.api;

import java.time.Instant;

/**
 * 触发器接口
 */
public interface Trigger {
    
    /**
     * 计算下一次执行时间
     * @param triggerContext 触发器上下文
     * @return 下一次执行时间，如果没有则返回null
     */
    Instant nextExecution(TriggerContext triggerContext);
}