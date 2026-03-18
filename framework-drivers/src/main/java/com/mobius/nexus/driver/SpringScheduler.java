package com.mobius.nexus.driver;

import com.mobius.nexus.api.*;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * Spring调度器适配�? */
public class SpringScheduler implements Scheduler {
    
    private final TaskScheduler taskScheduler;
    private final Map<ScheduledTask, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    
    public SpringScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("module-scheduler-");
        scheduler.initialize();
        this.taskScheduler = scheduler;
    }
    
    @Override
    public ScheduledTask schedule(Runnable task, Trigger trigger) {
        ScheduledFuture<?> future = taskScheduler.schedule(task, trigger -> {
            Instant next = trigger.nextExecution(new SimpleTriggerContext());
            return next;
        });
        return registerTask(future);
    }
    
    @Override
    public ScheduledTask schedule(Runnable task, Instant startTime) {
        ScheduledFuture<?> future = taskScheduler.schedule(task, startTime);
        return registerTask(future);
    }
    
    @Override
    public ScheduledTask scheduleAtFixedRate(Runnable task, Instant startTime, Duration period) {
        ScheduledFuture<?> future = taskScheduler.scheduleAtFixedRate(task, startTime, period.toMillis());
        return registerTask(future);
    }
    
    @Override
    public ScheduledTask scheduleWithFixedDelay(Runnable task, Instant startTime, Duration delay) {
        ScheduledFuture<?> future = taskScheduler.scheduleWithFixedDelay(task, startTime, delay.toMillis());
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
        if (taskScheduler instanceof ThreadPoolTaskScheduler) {
            ((ThreadPoolTaskScheduler) taskScheduler).shutdown();
        }
    }
    
    private ScheduledTask registerTask(ScheduledFuture<?> future) {
        SpringScheduledTask task = new SpringScheduledTask(future);
        scheduledTasks.put(task, future);
        return task;
    }
    
    private static class SpringScheduledTask implements ScheduledTask {
        private final ScheduledFuture<?> future;
        
        SpringScheduledTask(ScheduledFuture<?> future) {
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