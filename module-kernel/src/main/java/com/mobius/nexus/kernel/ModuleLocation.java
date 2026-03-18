package com.mobius.nexus.kernel;

import java.net.URL;
import java.nio.file.Path;

/**
 * 模块位置
 * 表示模块的来源位�? */
public class ModuleLocation {
    private final String location;
    private final Type type;
    
    private ModuleLocation(String location, Type type) {
        this.location = location;
        this.type = type;
    }
    
    public static ModuleLocation file(Path path) {
        return new ModuleLocation(path.toAbsolutePath().toString(), Type.FILE);
    }
    
    public static ModuleLocation file(String path) {
        return new ModuleLocation(path, Type.FILE);
    }
    
    public static ModuleLocation url(URL url) {
        return new ModuleLocation(url.toString(), Type.URL);
    }
    
    public static ModuleLocation url(String url) {
        return new ModuleLocation(url, Type.URL);
    }
    
    public static ModuleLocation repository(String moduleName, String version) {
        return new ModuleLocation(moduleName + ":" + version, Type.REPOSITORY);
    }
    
    public String getLocation() {
        return location;
    }
    
    public Type getType() {
        return type;
    }
    
    public enum Type {
        FILE,
        URL,
        REPOSITORY
    }
    
    @Override
    public String toString() {
        return type.name() + ":" + location;
    }
}