package com.mobius.nexus.kernel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 基础模块实现
 */
public class BaseModule implements Module {
    private final ModuleId moduleId;
    private final Map<String, String> dependencies;
    private final List<String> exports;
    private final List<String> imports;
    private final ClassLoader classLoader;
    private final String activatorClassName;
    
    private ModuleState state = ModuleState.INSTALLED;
    private final Map<Class<?>, List<Object>> services = new ConcurrentHashMap<>();
    private ModuleActivator activator;
    
    public BaseModule(ModuleId moduleId, Map<String, String> dependencies, 
                     List<String> exports, List<String> imports, 
                     ClassLoader classLoader) {
        this(moduleId, dependencies, exports, imports, classLoader, null);
    }
    
    public BaseModule(ModuleId moduleId, Map<String, String> dependencies, 
                     List<String> exports, List<String> imports, 
                     ClassLoader classLoader, String activatorClassName) {
        this.moduleId = moduleId;
        this.dependencies = Collections.unmodifiableMap(new HashMap<>(dependencies));
        this.exports = Collections.unmodifiableList(new ArrayList<>(exports));
        this.imports = Collections.unmodifiableList(new ArrayList<>(imports));
        this.classLoader = classLoader;
        this.activatorClassName = activatorClassName;
    }
    
    @Override
    public ModuleId getId() {
        return moduleId;
    }
    
    @Override
    public Map<String, String> getDependencies() {
        return dependencies;
    }
    
    @Override
    public List<String> getExports() {
        return exports;
    }
    
    @Override
    public List<String> getImports() {
        return imports;
    }
    
    @Override
    public ModuleState getState() {
        return state;
    }
    
    @Override
    public void start() throws ModuleException {
        if (state != ModuleState.INSTALLED && state != ModuleState.RESOLVED) {
            throw new ModuleException("Module cannot be started from state: " + state);
        }
        
        try {
            state = ModuleState.STARTING;
            
            // 查找并调用ModuleActivator（如果有）
            if (activatorClassName != null && !activatorClassName.isEmpty()) {
                try {
                    Class<?> activatorClass = classLoader.loadClass(activatorClassName);
                    if (ModuleActivator.class.isAssignableFrom(activatorClass)) {
                        activator = (ModuleActivator) activatorClass.getDeclaredConstructor().newInstance();
                        // 创建简单的ModuleContext
                        ModuleContext context = new SimpleModuleContext(this);
                        activator.start(context);
                    }
                } catch (Exception e) {
                    throw new ModuleException("Failed to instantiate activator: " + activatorClassName, e);
                }
            }
            
            state = ModuleState.ACTIVE;
        } catch (Exception e) {
            state = ModuleState.INSTALLED;
            throw new ModuleException("Failed to start module: " + moduleId, e);
        }
    }
    
    @Override
    public void stop() throws ModuleException {
        if (state != ModuleState.ACTIVE) {
            throw new ModuleException("Module cannot be stopped from state: " + state);
        }
        
        try {
            state = ModuleState.STOPPING;
            
            // 调用ModuleActivator.stop()（如果有）
            if (activator != null) {
                ModuleContext context = new SimpleModuleContext(this);
                activator.stop(context);
            }
            
            state = ModuleState.INSTALLED;
        } catch (Exception e) {
            state = ModuleState.ACTIVE;
            throw new ModuleException("Failed to stop module: " + moduleId, e);
        }
    }
    
    @Override
    public void update(Module newVersion) throws ModuleException {
        if (!moduleId.isSameModule(newVersion.getId())) {
            throw new ModuleException("Cannot update to different module: " + newVersion.getId());
        }
        
        // TODO: 实现热更新逻辑
        throw new UnsupportedOperationException("Hot update not yet implemented");
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> getServices(Class<T> serviceType) {
        List<Object> serviceList = services.get(serviceType);
        if (serviceList == null) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>(serviceList.size());
        for (Object service : serviceList) {
            if (serviceType.isInstance(service)) {
                result.add((T) service);
            }
        }
        return Collections.unmodifiableList(result);
    }
    
    @Override
    public void registerService(Object service, Class<?>... serviceTypes) {
        if (service == null) {
            throw new IllegalArgumentException("Service cannot be null");
        }
        
        if (serviceTypes == null || serviceTypes.length == 0) {
            // 注册所有接口
            Class<?>[] interfaces = service.getClass().getInterfaces();
            if (interfaces.length == 0) {
                throw new IllegalArgumentException("Service must implement at least one interface");
            }
            registerService(service, interfaces);
            return;
        }
        
        for (Class<?> serviceType : serviceTypes) {
            if (!serviceType.isInstance(service)) {
                throw new IllegalArgumentException(
                    "Service does not implement " + serviceType.getName());
            }
            
            services.computeIfAbsent(serviceType, k -> new CopyOnWriteArrayList<>())
                   .add(service);
        }
    }
    
    @Override
    public void unregisterService(Object service) {
        if (service == null) {
            return;
        }
        
        for (Map.Entry<Class<?>, List<Object>> entry : services.entrySet()) {
            entry.getValue().removeIf(s -> s == service);
        }
    }
    
    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }
    
    public void setState(ModuleState state) {
        this.state = state;
    }
}