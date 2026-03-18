package com.mobius.nexus.kernel;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模块描述�? * 包含模块的元数据信息
 */
public class ModuleDescriptor {
    private final ModuleId moduleId;
    private final Map<String, String> dependencies;
    private final List<String> exports;
    private final List<String> imports;
    private final String activatorClassName;
    
    private ModuleDescriptor(Builder builder) {
        this.moduleId = builder.moduleId;
        this.dependencies = Collections.unmodifiableMap(builder.dependencies);
        this.exports = Collections.unmodifiableList(builder.exports);
        this.imports = Collections.unmodifiableList(builder.imports);
        this.activatorClassName = builder.activatorClassName;
    }
    
    public ModuleId getModuleId() {
        return moduleId;
    }
    
    public Map<String, String> getDependencies() {
        return dependencies;
    }
    
    public List<String> getExports() {
        return exports;
    }
    
    public List<String> getImports() {
        return imports;
    }
    
    public String getActivatorClassName() {
        return activatorClassName;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private ModuleId moduleId;
        private Map<String, String> dependencies = new HashMap<>();
        private List<String> exports = Collections.emptyList();
        private List<String> imports = Collections.emptyList();
        private String activatorClassName;
        
        public Builder moduleId(ModuleId moduleId) {
            this.moduleId = moduleId;
            return this;
        }
        
        public Builder moduleId(String name, String version) {
            this.moduleId = ModuleId.of(name, version);
            return this;
        }
        
        public Builder dependency(String moduleName, String versionRange) {
            this.dependencies.put(moduleName, versionRange);
            return this;
        }
        
        public Builder dependencies(Map<String, String> dependencies) {
            this.dependencies.putAll(dependencies);
            return this;
        }
        
        public Builder exports(List<String> exports) {
            this.exports = exports;
            return this;
        }
        
        public Builder imports(List<String> imports) {
            this.imports = imports;
            return this;
        }
        
        public Builder activatorClassName(String activatorClassName) {
            this.activatorClassName = activatorClassName;
            return this;
        }
        
        public ModuleDescriptor build() {
            if (moduleId == null) {
                throw new IllegalStateException("ModuleId must be specified");
            }
            return new ModuleDescriptor(this);
        }
    }
}