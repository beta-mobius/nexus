package com.mobius.nexus.kernel;

import com.mobius.nexus.api.BeanContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * 简单的模块上下文实�? */
public class SimpleModuleContext implements ModuleContext {
    
    private final Module module;
    private final Map<String, String> properties = new HashMap<>();
    
    public SimpleModuleContext(Module module) {
        this.module = module;
    }
    
    @Override
    public Module getModule() {
        return module;
    }
    
    @Override
    public BeanContainer getBeanContainer() {
        // 简化实现，返回null
        return null;
    }
    
    @Override
    public ServiceRegistration registerService(Object service, Class<?>... serviceTypes) {
        module.registerService(service, serviceTypes);
        return new SimpleServiceRegistration(module, service, serviceTypes);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getService(Class<T> serviceType) {
        java.util.List<T> services = module.getServices(serviceType);
        return services.isEmpty() ? null : services.get(0);
    }
    
    @Override
    public String getProperty(String key, String defaultValue) {
        return properties.getOrDefault(key, defaultValue);
    }
    
    @Override
    public String getProperty(String key) {
        return properties.get(key);
    }
    
    /**
     * 设置属�?     */
    public void setProperty(String key, String value) {
        properties.put(key, value);
    }
    
    /**
     * 简单的服务注册实现
     */
    private static class SimpleServiceRegistration implements ServiceRegistration {
        private final Module module;
        private final Object service;
        private final Class<?>[] serviceTypes;
        
        SimpleServiceRegistration(Module module, Object service, Class<?>[] serviceTypes) {
            this.module = module;
            this.service = service;
            this.serviceTypes = serviceTypes;
        }
        
        @Override
        public void unregister() {
            module.unregisterService(service);
        }
        
        @Override
        public Object getService() {
            return service;
        }
        
        @Override
        public Class<?>[] getServiceTypes() {
            return serviceTypes;
        }
    }
}