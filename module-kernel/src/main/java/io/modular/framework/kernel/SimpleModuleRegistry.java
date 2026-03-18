package io.modular.framework.kernel;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 简单的内存模块注册表实现
 */
public class SimpleModuleRegistry implements ModuleRegistry {
    private final Map<ModuleId, Module> modules = new HashMap<>();
    
    @Override
    public void register(Module module) {
        if (module == null) {
            throw new IllegalArgumentException("Module cannot be null");
        }
        
        ModuleId moduleId = module.getId();
        if (modules.containsKey(moduleId)) {
            throw new IllegalStateException("Module already registered: " + moduleId);
        }
        
        modules.put(moduleId, module);
    }
    
    @Override
    public void unregister(ModuleId moduleId) {
        modules.remove(moduleId);
    }
    
    @Override
    public Optional<Module> getModule(ModuleId moduleId) {
        return Optional.ofNullable(modules.get(moduleId));
    }
    
    @Override
    public Optional<Module> getModule(String moduleName) {
        // 返回最新版本
        return modules.values().stream()
            .filter(m -> m.getName().equals(moduleName))
            .sorted((m1, m2) -> {
                Version v1 = m1.getId().getVersion();
                Version v2 = m2.getId().getVersion();
                return v2.compareTo(v1); // 降序排序，最新版本在前
            })
            .findFirst();
    }
    
    @Override
    public Collection<Module> getAllModules() {
        return Collections.unmodifiableCollection(modules.values());
    }
    
    @Override
    public boolean contains(ModuleId moduleId) {
        return modules.containsKey(moduleId);
    }
    
    @Override
    public int size() {
        return modules.size();
    }
    
    @Override
    public void clear() {
        modules.clear();
    }
}