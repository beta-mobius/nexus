package io.modular.framework.kernel;

/**
 * 模块激活器接口
 * 模块可以实现此接口以接收生命周期回调
 */
public interface ModuleActivator {
    
    /**
     * 模块启动时调用
     * @param context 模块上下文
     * @throws Exception 如果启动失败
     */
    void start(ModuleContext context) throws Exception;
    
    /**
     * 模块停止时调用
     * @param context 模块上下文
     * @throws Exception 如果停止失败
     */
    void stop(ModuleContext context) throws Exception;
}