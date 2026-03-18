package com.mobius.nexus.driver;

import java.util.Map;

/**
 * 框架驱动接口
 * 将特定框架（如Spring、Guice）适配为通用服务API
 */
public interface FrameworkDriver {
    
    /**
     * 获取驱动名称
     * @return 驱动名称，如 "spring", "guice", "lightweight"
     */
    String getName();
    
    /**
     * 创建服务映射
     * @param config 驱动配置
     * @return 服务映射：service-api接口 -> 具体实现
     */
    Map<Class<?>, Object> createServices(DriverConfig config);
}