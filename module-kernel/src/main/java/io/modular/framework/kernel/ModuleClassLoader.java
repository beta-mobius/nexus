package io.modular.framework.kernel;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模块类加载器
 * 为每个模块提供独立的类加载环境
 * 包含性能优化：类加载缓存、资源缓存
 */
public class ModuleClassLoader extends URLClassLoader {
    private final ModuleId moduleId;
    private final List<ModuleClassLoader> dependencyClassLoaders;
    
    // 性能优化：缓存类加载器查找结果
    private final ConcurrentHashMap<String, ClassLoader> classLoaderCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, URL> resourceCache = new ConcurrentHashMap<>();
    private final Object cacheLock = new Object();
    
    /**
     * 创建模块类加载器
     * @param urls 模块JAR的URL
     * @param parent 父类加载器
     * @param moduleId 模块标识
     * @param dependencyClassLoaders 依赖模块的类加载器
     */
    public ModuleClassLoader(URL[] urls, ClassLoader parent, ModuleId moduleId,
                            List<ModuleClassLoader> dependencyClassLoaders) {
        super(urls, parent);
        this.moduleId = moduleId;
        this.dependencyClassLoaders = dependencyClassLoaders;
    }
    
    public ModuleId getModuleId() {
        return moduleId;
    }
    
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // 首先检查是否已加载
        Class<?> loadedClass = findLoadedClass(name);
        if (loadedClass != null) {
            if (resolve) {
                resolveClass(loadedClass);
            }
            return loadedClass;
        }
        
        // 检查缓存中是否有已知的加载器
        ClassLoader cachedLoader = classLoaderCache.get(name);
        if (cachedLoader != null) {
            if (cachedLoader == this) {
                // 从自己加载
                Class<?> clazz = findClass(name);
                if (resolve) {
                    resolveClass(clazz);
                }
                return clazz;
            } else if (cachedLoader instanceof ModuleClassLoader) {
                // 从依赖加载器加载
                Class<?> clazz = ((ModuleClassLoader) cachedLoader).loadClass(name, resolve);
                return clazz;
            } else {
                // 从父类加载器加载
                Class<?> clazz = cachedLoader.loadClass(name);
                if (resolve && clazz != null) {
                    resolveClass(clazz);
                }
                return clazz;
            }
        }
        
        // 尝试从依赖模块加载
        if (dependencyClassLoaders != null) {
            for (ModuleClassLoader dependencyLoader : dependencyClassLoaders) {
                try {
                    // 检查依赖加载器是否已加载此类
                    Class<?> loadedInDep = dependencyLoader.findLoadedClass(name);
                    if (loadedInDep != null) {
                        classLoaderCache.put(name, dependencyLoader);
                        if (resolve) {
                            resolveClass(loadedInDep);
                        }
                        return loadedInDep;
                    }
                    
                    // 尝试加载
                    Class<?> clazz = dependencyLoader.loadClass(name, false);
                    if (clazz != null) {
                        // 缓存结果
                        classLoaderCache.put(name, dependencyLoader);
                        if (resolve) {
                            resolveClass(clazz);
                        }
                        return clazz;
                    }
                } catch (ClassNotFoundException e) {
                    // 继续尝试下一个依赖
                }
            }
        }
        
        // 尝试从自己的URL加载
        try {
            Class<?> clazz = findClass(name);
            // 缓存结果（自己加载）
            classLoaderCache.put(name, this);
            if (resolve) {
                resolveClass(clazz);
            }
            return clazz;
        } catch (ClassNotFoundException e) {
            // 最后委托给父类加载器
            try {
                Class<?> clazz = super.loadClass(name, resolve);
                // 缓存父类加载器
                classLoaderCache.put(name, getParent());
                return clazz;
            } catch (ClassNotFoundException e2) {
                // 缓存失败结果（null表示无法加载）
                classLoaderCache.put(name, null);
                throw e2;
            }
        }
    }
    
    @Override
    public URL getResource(String name) {
        // 检查缓存
        URL cachedResource = resourceCache.get(name);
        if (cachedResource != null) {
            // 特殊标记：NULL_URL表示资源不存在
            return cachedResource == NULL_URL ? null : cachedResource;
        }
        
        // 首先从自己的URL查找资源
        URL resource = findResource(name);
        if (resource != null) {
            resourceCache.put(name, resource);
            return resource;
        }
        
        // 从依赖模块查找资源
        if (dependencyClassLoaders != null) {
            for (ModuleClassLoader dependencyLoader : dependencyClassLoaders) {
                resource = dependencyLoader.getResource(name);
                if (resource != null) {
                    resourceCache.put(name, resource);
                    return resource;
                }
            }
        }
        
        // 最后委托给父类加载器
        resource = super.getResource(name);
        if (resource != null) {
            resourceCache.put(name, resource);
        } else {
            // 缓存不存在的结果
            resourceCache.put(name, NULL_URL);
        }
        return resource;
    }
    
    /**
     * 清理缓存（内存优化）
     */
    public void clearCache() {
        classLoaderCache.clear();
        resourceCache.clear();
    }
    
    /**
     * 清理特定资源的缓存
     */
    public void clearCache(String name) {
        classLoaderCache.remove(name);
        resourceCache.remove(name);
    }
    
    // 特殊标记：表示资源不存在
    private static final URL NULL_URL;
    static {
        try {
            NULL_URL = new URL("file:///dev/null");
        } catch (Exception e) {
            throw new RuntimeException("Failed to create NULL_URL", e);
        }
    }
}