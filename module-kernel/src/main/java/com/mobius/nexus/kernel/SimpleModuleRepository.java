package com.mobius.nexus.kernel;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 简单的文件系统模块仓库实现
 */
public class SimpleModuleRepository implements ModuleRepository {
    private final Path repositoryRoot;
    private final Map<ModuleId, Path> modulePaths = new HashMap<>();
    
    public SimpleModuleRepository(Path repositoryRoot) {
        this.repositoryRoot = repositoryRoot;
        initialize();
    }
    
    /**
     * 初始化仓库，扫描已有模块
     */
    private void initialize() {
        if (!Files.exists(repositoryRoot)) {
            try {
                Files.createDirectories(repositoryRoot);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create repository root: " + repositoryRoot, e);
            }
            return;
        }
        
        // 扫描modules目录
        Path modulesDir = repositoryRoot.resolve("modules");
        if (Files.exists(modulesDir)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(modulesDir)) {
                for (Path moduleDir : stream) {
                    if (Files.isDirectory(moduleDir)) {
                        scanModuleDir(moduleDir);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to scan repository", e);
            }
        }
    }
    
    /**
     * 扫描模块目录
     */
    private void scanModuleDir(Path moduleDir) {
        String moduleName = moduleDir.getFileName().toString();
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(moduleDir)) {
            for (Path versionDir : stream) {
                if (Files.isDirectory(versionDir)) {
                    String versionStr = versionDir.getFileName().toString();
                    try {
                        Version version = Version.parse(versionStr);
                        ModuleId moduleId = ModuleId.of(moduleName, version);
                        modulePaths.put(moduleId, versionDir);
                    } catch (Exception e) {
                        // 忽略无效版本目录
                    }
                }
            }
        } catch (IOException e) {
            // 忽略扫描错误
        }
    }
    
    @Override
    public void storeModule(ModuleArchive archive) {
        ModuleId moduleId = archive.getDescriptor().getModuleId();
        Path moduleDir = repositoryRoot.resolve("modules")
            .resolve(moduleId.getSymbolicName())
            .resolve(moduleId.getVersion().toString());
        
        try {
            Files.createDirectories(moduleDir);
            Files.copy(archive.getJarPath(), moduleDir.resolve(archive.getJarPath().getFileName()));
            modulePaths.put(moduleId, moduleDir);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store module: " + moduleId, e);
        }
    }
    
    @Override
    public ModuleArchive retrieveModule(ModuleId moduleId) {
        Path modulePath = modulePaths.get(moduleId);
        if (modulePath == null || !Files.exists(modulePath)) {
            return null;
        }
        
        // 查找JAR文件
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(modulePath, "*.jar")) {
            for (Path jarPath : stream) {
                // 解析描述�?                ModuleDescriptor descriptor = parseDescriptor(jarPath);
                return new ModuleArchive(jarPath, descriptor);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to retrieve module: " + moduleId, e);
        }
        
        return null;
    }
    
    /**
     * 从JAR文件解析描述符（简化版本）
     */
    private ModuleDescriptor parseDescriptor(Path jarPath) {
        // 简化实现：从文件名解析
        String fileName = jarPath.getFileName().toString();
        String baseName = fileName.replace(".jar", "");
        
        // 尝试从文件名提取名称和版�?        String[] parts = baseName.split("-");
        String name = parts[0];
        String version = "1.0.0";
        
        if (parts.length > 1) {
            // 假设最后一个部分是版本�?            version = parts[parts.length - 1];
        }
        
        return ModuleDescriptor.builder()
            .moduleId(name, version)
            .build();
    }
    
    @Override
    public List<Version> getAvailableVersions(String moduleName) {
        return modulePaths.keySet().stream()
            .filter(id -> id.getSymbolicName().equals(moduleName))
            .map(ModuleId::getVersion)
            .sorted(Comparator.reverseOrder())
            .collect(Collectors.toList());
    }
    
    @Override
    public ModuleId resolveVersion(String moduleName, VersionRange versionRange) {
        return getAvailableVersions(moduleName).stream()
            .filter(v -> versionRange.contains(v))
            .max(Comparator.naturalOrder())
            .map(v -> ModuleId.of(moduleName, v))
            .orElse(null);
    }
    
    @Override
    public DependencyResolution resolveDependencies(ModuleDescriptor descriptor) {
        Map<String, String> deps = descriptor.getDependencies();
        List<ModuleId> resolved = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        
        for (Map.Entry<String, String> entry : deps.entrySet()) {
            String depName = entry.getKey();
            String versionRangeStr = entry.getValue();
            
            try {
                VersionRange versionRange = VersionRange.parse(versionRangeStr);
                ModuleId depId = resolveVersion(depName, versionRange);
                
                if (depId == null) {
                    errors.add("Cannot resolve dependency: " + depName + " " + versionRangeStr);
                } else {
                    resolved.add(depId);
                }
            } catch (Exception e) {
                errors.add("Failed to resolve " + depName + ": " + e.getMessage());
            }
        }
        
        if (!errors.isEmpty()) {
            return DependencyResolution.failure(String.join("; ", errors));
        }
        
        return DependencyResolution.success(resolved);
    }
}