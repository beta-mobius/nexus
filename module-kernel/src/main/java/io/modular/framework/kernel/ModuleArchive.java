package io.modular.framework.kernel;

import java.nio.file.Path;

/**
 * 模块归档
 * 表示模块的打包文件（通常是JAR文件）
 */
public class ModuleArchive {
    private final Path jarPath;
    private final ModuleDescriptor descriptor;
    
    public ModuleArchive(Path jarPath, ModuleDescriptor descriptor) {
        this.jarPath = jarPath;
        this.descriptor = descriptor;
    }
    
    public Path getJarPath() {
        return jarPath;
    }
    
    public ModuleDescriptor getDescriptor() {
        return descriptor;
    }
}