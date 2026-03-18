package io.modular.framework.kernel;

/**
 * 服务注册接口
 * 表示一个已注册的服务，可用于取消注册
 */
public interface ServiceRegistration {
    
    /**
     * 取消注册服务
     */
    void unregister();
    
    /**
     * 获取服务实例
     */
    Object getService();
    
    /**
     * 获取服务接口类型
     */
    Class<?>[] getServiceTypes();
}