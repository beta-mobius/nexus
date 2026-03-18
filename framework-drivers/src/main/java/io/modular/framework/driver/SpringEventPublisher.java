package io.modular.framework.driver;

import io.modular.framework.api.ApplicationListener;
import io.modular.framework.api.EventPublisher;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.Executor;

/**
 * Spring事件发布器适配器
 */
public class SpringEventPublisher implements EventPublisher {
    
    private final ApplicationContext applicationContext;
    
    public SpringEventPublisher(ApplicationContext context) {
        this.applicationContext = context;
    }
    
    @Override
    public void publishEvent(Object event) {
        applicationContext.publishEvent(event);
    }
    
    @Override
    public void publishEventAsync(Object event) {
        // 使用Spring的异步事件发布
        applicationContext.publishEvent(event);
    }
    
    @Override
    public void publishEventAsync(Object event, Executor executor) {
        executor.execute(() -> applicationContext.publishEvent(event));
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void addApplicationListener(ApplicationListener<?> listener) {
        // Spring的事件监听器通常通过注解或配置注册
        // 这里简化实现
        throw new UnsupportedOperationException(
            "Dynamic listener registration not supported in this adapter");
    }
    
    @Override
    public void removeApplicationListener(ApplicationListener<?> listener) {
        throw new UnsupportedOperationException(
            "Dynamic listener removal not supported in this adapter");
    }
}