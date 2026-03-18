package io.modular.framework.driver;

import io.modular.framework.api.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Spring框架驱动实现
 * 将Spring能力适配为通用服务API
 */
public class SpringFrameworkDriver implements FrameworkDriver {
    
    @Override
    public String getName() {
        return "spring";
    }
    
    @Override
    public Map<Class<?>, Object> createServices(DriverConfig config) {
        Map<Class<?>, Object> services = new HashMap<>();
        
        // 创建Spring ApplicationContext
        ApplicationContext context = createApplicationContext(config);
        
        // 包装Spring能力为通用API
        services.put(BeanContainer.class, new SpringBeanContainer(context));
        services.put(TransactionManager.class, new SpringTransactionManager(context));
        services.put(EventPublisher.class, new SpringEventPublisher(context));
        services.put(AspectManager.class, new SpringAspectManager(context));
        services.put(Scheduler.class, new SpringScheduler(context));
        services.put(Validator.class, new SpringValidator(context));
        
        return services;
    }
    
    private ApplicationContext createApplicationContext(DriverConfig config) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        
        // 解析配置，注册Bean等
        String basePackages = config.getProperty("scan.packages", "");
        if (!basePackages.isEmpty()) {
            context.scan(basePackages.split(","));
        }
        
        // 应用配置文件
        String configLocation = config.getProperty("config.location");
        if (configLocation != null && !configLocation.isEmpty()) {
            context.setConfigLocation(configLocation);
        }
        
        context.refresh();
        return context;
    }
}