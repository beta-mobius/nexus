package com.mobius.nexus.kernel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 模块接口
 * 表示一个可加载、可管理的模块单元
 */
public interface Module {
    
    /**
     * 获取模块标识符
     */
    ModuleId getId();
    
    /**
     * 获取模块名称（符号名称的别名）
     */
    default String getName() {
        return getId().getName();
    }
    
    /**
     * 获取模块版本
     */
    default String getVersion() {
        return getId().getVersion().toString();
    }
    
    /**
     * 获取模块依赖
     * @return 依赖映射，key为模块名称，value为版本范围
     */
    Map<String, String> getDependencies();
    
    /**
     * 获取模块依赖对象列表
     * @return 模块依赖对象列表
     */
    default List<ModuleDependency> getDependencyObjects() {
        List<ModuleDependency> dependencies = new ArrayList<>();
        for (Map.Entry<String, String> entry : getDependencies().entrySet()) {
            try {
                dependencies.add(ModuleDependency.parse(entry.getKey() + ":" + entry.getValue()));
            } catch (Exception e) {
                // 忽略解析失败的依赖
            }
        }
        return dependencies;
    }
    
    /**
     * 获取导出的包
     * @return 导出的包名列表
     */
    List<String> getExports();
    
    /**
     * 获取导入的包
     * @return 导入的包名列表
     */
    List<String> getImports();
    
    /**
     * 获取模块当前状态
     */
    ModuleState getState();
    
    /**
     * 启动模块
     * @throws ModuleException 如果启动失败
     */
    void start() throws ModuleException;
    
    /**
     * 停止模块
     * @throws ModuleException 如果停止失败
     */
    void stop() throws ModuleException;
    
    /**
     * 更新模块
     * @param newVersion 新版本模块
     * @throws ModuleException 如果更新失败
     */
    void update(Module newVersion) throws ModuleException;
    
    /**
     * 获取指定类型的服务
     * @param serviceType 服务类型
     * @param <T> 服务类型泛型
     * @return 服务实例列表
     */
    <T> List<T> getServices(Class<T> serviceType);
    
    /**
     * 注册服务
     * @param service 服务实例
     * @param serviceTypes 服务接口类型
     */
    void registerService(Object service, Class<?>... serviceTypes);
    
    /**
     * 注销服务
     * @param service 服务实例
     */
    void unregisterService(Object service);
    
    /**
     * 获取模块类加载器
     */
    ClassLoader getClassLoader();
}