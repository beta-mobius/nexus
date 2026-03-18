package com.mobius.nexus.driver.lightweight;

import com.mobius.nexus.api.*;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

/**
 * 简单调度器实现
 * 基于ScheduledThreadPoolExecutor的轻量级调度
 */
public class SimpleScheduler implements Scheduler {
    
    private final ScheduledExecutorService executor;
    private final Map<ScheduledTask, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    
    public SimpleScheduler() {
        this.executor = Executors.newScheduledThreadPool(4, 
            r -> new Thread(r, "simple-scheduler-thread"));
    }
    
    public SimpleScheduler(int poolSize) {
        this.executor = Executors.newScheduledThreadPool(poolSize,
            r -> new Thread(r, "simple-scheduler-thread"));
    }
    
    @Override
    public ScheduledTask schedule(Runnable task, Trigger trigger) {
        ScheduledFuture<?> future = executor.schedule(
            task, 
            getDelayForTrigger(trigger), 
            TimeUnit.MILLISECONDS
        );
        
        return registerTask(future);
    }
    
    @Override
    public ScheduledTask schedule(Runnable task, Instant startTime) {
        long delay = Math.max(0, startTime.toEpochMilli() - System.currentTimeMillis());
        ScheduledFuture<?> future = executor.schedule(task, delay, TimeUnit.MILLISECONDS);
        return registerTask(future);
    }
    
    @Override
    public ScheduledTask scheduleAtFixedRate(Runnable task, Instant startTime, Duration period) {
        long initialDelay = Math.max(0, startTime.toEpochMilli() - System.currentTimeMillis());
        ScheduledFuture<?> future = executor.scheduleAtFixedRate(
            task, 
            initialDelay, 
            period.toMillis(), 
            TimeUnit.MILLISECONDS
        );
        return registerTask(future);
    }
    
    @Override
    public ScheduledTask scheduleWithFixedDelay(Runnable task, Instant startTime, Duration delay) {
        long initialDelay = Math.max(0, startTime.toEpochMilli() - System.currentTimeMillis());
        ScheduledFuture<?> future = executor.scheduleWithFixedDelay(
            task, 
            initialDelay, 
            delay.toMillis(), 
            TimeUnit.MILLISECONDS
        );
        return registerTask(future);
    }
    
    @Override
    public void cancel(ScheduledTask task) {
        ScheduledFuture<?> future = scheduledTasks.remove(task);
        if (future != null) {
            future.cancel(true);
        }
    }
    
    @Override
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    private ScheduledTask registerTask(ScheduledFuture<?> future) {
        SimpleScheduledTask task = new SimpleScheduledTask(future);
        scheduledTasks.put(task, future);
        return task;
    }
    
    private long getDelayForTrigger(Trigger trigger) {
        Instant next = trigger.nextExecution(new SimpleTriggerContext());
        return Math.max(0, next.toEpochMilli() - System.currentTimeMillis());
    }
    
    /**
     * 简单定时任务实�?     */
    private static class SimpleScheduledTask implements ScheduledTask {
        private final ScheduledFuture<?> future;
        
        SimpleScheduledTask(ScheduledFuture<?> future) {
            this.future = future;
        }
        
        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return future.cancel(mayInterruptIfRunning);
        }
        
        @Override
        public boolean isCancelled() {
            return future.isCancelled();
        }
        
        @Override
        public boolean isDone() {
            return future.isDone();
        }
    }
    
    /**
     * 简单触发器上下�?     */
    private static class SimpleTriggerContext implements TriggerContext {
        @Override
        public Instant lastScheduledExecutionTime() {
            return Instant.now();
        }
        
        @Override
        public Instant lastActualExecutionTime() {
            return Instant.now();
        }
        
        @Override
        public Instant lastCompletionTime() {
            return Instant.now();
        }
    }
}