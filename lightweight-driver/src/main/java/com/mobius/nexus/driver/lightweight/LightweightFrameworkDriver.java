package com.mobius.nexus.driver.lightweight;

import com.mobius.nexus.api.*;
import com.mobius.nexus.driver.DriverConfig;
import com.mobius.nexus.driver.FrameworkDriver;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 轻量级框架驱�? * 不依赖Spring的简单实现，适用于小型应用或测试环境
 */
public class LightweightFrameworkDriver implements FrameworkDriver {
    
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private DriverConfig config;
    
    private SimpleBeanContainer beanContainer;
    private SimpleTransactionManager transactionManager;
    private SimpleEventPublisher eventPublisher;
    private SimpleAspectManager aspectManager;
    private SimpleScheduler scheduler;
    private SimpleValidator validator;
    
    @Override
    public void initialize(DriverConfig config) {
        if (!initialized.compareAndSet(false, true)) {
            throw new IllegalStateException("Driver already initialized");
        }
        
        this.config = config;
        
        // 创建轻量级组�?        this.beanContainer = new SimpleBeanContainer();
        this.transactionManager = new SimpleTransactionManager();
        this.eventPublisher = new SimpleEventPublisher();
        this.aspectManager = new SimpleAspectManager();
        this.scheduler = new SimpleScheduler();
        this.validator = new SimpleValidator();
        
        System.out.println("LightweightFrameworkDriver initialized with config: " + config);
    }
    
    @Override
    public BeanContainer getBeanContainer() {
        checkInitialized();
        return beanContainer;
    }
    
    @Override
    public TransactionManager getTransactionManager() {
        checkInitialized();
        return transactionManager;
    }
    
    @Override
    public EventPublisher getEventPublisher() {
        checkInitialized();
        return eventPublisher;
    }
    
    @Override
    public AspectManager getAspectManager() {
        checkInitialized();
        return aspectManager;
    }
    
    @Override
    public Scheduler getScheduler() {
        checkInitialized();
        return scheduler;
    }
    
    @Override
    public Validator getValidator() {
        checkInitialized();
        return validator;
    }
    
    @Override
    public void shutdown() {
        if (initialized.compareAndSet(true, false)) {
            if (scheduler != null) {
                scheduler.shutdown();
            }
            System.out.println("LightweightFrameworkDriver shutdown");
        }
    }
    
    private void checkInitialized() {
        if (!initialized.get()) {
            throw new IllegalStateException("Driver not initialized. Call initialize() first.");
        }
    }
}