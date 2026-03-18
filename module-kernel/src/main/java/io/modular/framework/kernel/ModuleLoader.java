package io.modular.framework.kernel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 模块加载器
 * 负责从各种来源加载模块
 */
public class ModuleLoader {
    private final ModuleRegistry registry;
    private final ModuleRepository repository;
    
    public ModuleLoader(ModuleRegistry registry, ModuleRepository repository) {
        this.registry = registry;
        this.repository = repository;
    }
    
    /**
     * 加载模块
     * @param location 模块位置
     * @return 加载的模块实例
     * @throws ModuleException 如果加载失败
     */
    public Module loadModule(ModuleLocation location) throws ModuleException {
        try {
            // 1. 加载模块归档
            ModuleArchive archive = loadArchive(location);
            
            // 2. 解析模块描述符
            ModuleDescriptor descriptor = parseDescriptor(archive);
            
            // 3. 解析依赖
            DependencyResolution resolution = repository.resolveDependencies(descriptor);
            if (!resolution.isSuccessful()) {
                throw new ModuleException("Dependency resolution failed: " + resolution.getErrorMessage());
            }
            
            // 4. 创建类加载器
            ClassLoader classLoader = createClassLoader(archive, resolution.getResolvedDependencies());
            
            // 5. 创建模块实例
            Module module = createModule(descriptor, classLoader);
            
            // 6. 注册模块
            registry.register(module);
            
            return module;
        } catch (Exception e) {
            throw new ModuleException("Failed to load module from: " + location, e);
        }
    }
    
    /**
     * 加载模块归档
     */
    private ModuleArchive loadArchive(ModuleLocation location) throws IOException {
        Path jarPath;
        
        switch (location.getType()) {
            case FILE:
                jarPath = Paths.get(location.getLocation());
                break;
            case URL:
                // TODO: 支持URL下载
                throw new UnsupportedOperationException("URL loading not yet implemented");
            case REPOSITORY:
                // TODO: 支持仓库下载
                throw new UnsupportedOperationException("Repository loading not yet implemented");
            default:
                throw new IllegalArgumentException("Unknown location type: " + location.getType());
        }
        
        if (!Files.exists(jarPath)) {
            throw new IOException("Module file not found: " + jarPath);
        }
        
        // 解析描述符
        ModuleDescriptor descriptor = parseFromJar(jarPath);
        
        return new ModuleArchive(jarPath, descriptor);
    }
    
    /**
     * 解析模块描述符
     */
    private ModuleDescriptor parseDescriptor(ModuleArchive archive) {
        return archive.getDescriptor();
    }
    
    /**
     * 从JAR文件解析模块描述符
     */
    private ModuleDescriptor parseFromJar(Path jarPath) throws IOException {
        Properties props = new Properties();
        
        try (JarFile jarFile = new JarFile(jarPath.toFile())) {
            JarEntry entry = jarFile.getJarEntry("META-INF/module.properties");
            if (entry != null) {
                try (InputStream is = jarFile.getInputStream(entry)) {
                    props.load(is);
                }
            }
        }
        
        // 如果没有module.properties，使用文件名作为默认
        String fileName = jarPath.getFileName().toString();
        String moduleName = props.getProperty("module.name", 
            fileName.replace(".jar", ""));
        String version = props.getProperty("module.version", "1.0.0");
        
        // 解析依赖（支持格式：module:version[?][*]）
        Map<String, String> dependencies = new HashMap<>();
        String depsStr = props.getProperty("module.dependencies", "");
        if (!depsStr.trim().isEmpty()) {
            for (String dep : depsStr.split(",")) {
                dep = dep.trim();
                if (!dep.isEmpty()) {
                    // 保留原始字符串，包括可选和传递标记
                    // ModuleDependency.parse()会处理这些标记
                    String[] parts = dep.split(":");
                    if (parts.length >= 2) {
                        String moduleNamePart = parts[0].trim();
                        // 重新组合版本部分（可能包含:，如[1.0,2.0)）
                        StringBuilder versionBuilder = new StringBuilder();
                        for (int i = 1; i < parts.length; i++) {
                            if (i > 1) versionBuilder.append(":");
                            versionBuilder.append(parts[i]);
                        }
                        String versionPart = versionBuilder.toString().trim();
                        dependencies.put(moduleNamePart, versionPart);
                    }
                }
            }
        }
        
        // 解析导出包
        List<String> exports = new ArrayList<>();
        String exportsStr = props.getProperty("module.exports", "");
        if (!exportsStr.trim().isEmpty()) {
            for (String pkg : exportsStr.split(",")) {
                pkg = pkg.trim();
                if (!pkg.isEmpty()) {
                    exports.add(pkg);
                }
            }
        }
        
        // 解析导入包
        List<String> imports = new ArrayList<>();
        String importsStr = props.getProperty("module.imports", "");
        if (!importsStr.trim().isEmpty()) {
            for (String pkg : importsStr.split(",")) {
                pkg = pkg.trim();
                if (!pkg.isEmpty()) {
                    imports.add(pkg);
                }
            }
        }
        
        // 解析激活器类名
        String activatorClassName = props.getProperty("module.activator", "");
        
        return ModuleDescriptor.builder()
                .moduleId(moduleName, version)
                .dependencies(dependencies)
                .exports(exports)
                .imports(imports)
                .activatorClassName(activatorClassName.isEmpty() ? null : activatorClassName)
                .build();
    }
    
    /**
     * 创建模块类加载器
     */
    private ClassLoader createClassLoader(ModuleArchive archive, 
                                          List<ModuleId> dependencies) throws Exception {
        Path jarPath = archive.getJarPath();
        URL jarUrl = jarPath.toUri().toURL();
        
        // 获取依赖模块的类加载器
        List<ModuleClassLoader> depLoaders = new ArrayList<>();
        for (ModuleId depId : dependencies) {
            registry.getModule(depId).ifPresent(m -> {
                if (m.getClassLoader() instanceof ModuleClassLoader) {
                    depLoaders.add((ModuleClassLoader) m.getClassLoader());
                }
            });
        }
        
        // 创建模块类加载器
        return new ModuleClassLoader(
            new URL[] { jarUrl },
            getClass().getClassLoader(),
            archive.getDescriptor().getModuleId(),
            depLoaders
        );
    }
    
    /**
     * 创建模块实例
     */
    private Module createModule(ModuleDescriptor descriptor, ClassLoader classLoader) {
        return new BaseModule(
            descriptor.getModuleId(),
            descriptor.getDependencies(),
            descriptor.getExports(),
            descriptor.getImports(),
            classLoader,
            descriptor.getActivatorClassName()
        );
    }
    
    /**
     * 启动模块
     */
    public void startModule(ModuleId moduleId) throws ModuleException {
        Module module = registry.getModule(moduleId)
            .orElseThrow(() -> new ModuleException("Module not found: " + moduleId));
        module.start();
    }
    
    /**
     * 停止模块
     */
    public void stopModule(ModuleId moduleId) throws ModuleException {
        Module module = registry.getModule(moduleId)
            .orElseThrow(() -> new ModuleException("Module not found: " + moduleId));
        module.stop();
    }
    
    /**
     * 卸载模块
     */
    public void unloadModule(ModuleId moduleId) throws ModuleException {
        Module module = registry.getModule(moduleId)
            .orElseThrow(() -> new ModuleException("Module not found: " + moduleId));
        
        if (module.getState() == ModuleState.ACTIVE) {
            module.stop();
        }
        
        registry.unregister(moduleId);
    }
}