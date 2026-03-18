package com.mobius.nexus.driver;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 驱动配置�? */
public class DriverConfig {
    
    private final Map<String, String> properties;
    
    public DriverConfig() {
        this.properties = new HashMap<>();
    }
    
    public DriverConfig(Map<String, String> properties) {
        this.properties = new HashMap<>(properties);
    }
    
    public DriverConfig(Properties props) {
        this.properties = new HashMap<>();
        for (String key : props.stringPropertyNames()) {
            this.properties.put(key, props.getProperty(key));
        }
    }
    
    /**
     * 获取配置属�?     * @param key 属性键
     * @param defaultValue 默认�?     * @return 属性�?     */
    public String getProperty(String key, String defaultValue) {
        return properties.getOrDefault(key, defaultValue);
    }
    
    /**
     * 获取配置属�?     * @param key 属性键
     * @return 属性值，如果不存在返回null
     */
    public String getProperty(String key) {
        return properties.get(key);
    }
    
    /**
     * 获取整数配置属�?     * @param key 属性键
     * @param defaultValue 默认�?     * @return 属性�?     */
    public int getIntProperty(String key, int defaultValue) {
        String value = properties.get(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * 获取布尔配置属�?     * @param key 属性键
     * @param defaultValue 默认�?     * @return 属性�?     */
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = properties.get(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }
    
    /**
     * 设置配置属�?     */
    public void setProperty(String key, String value) {
        properties.put(key, value);
    }
    
    /**
     * 获取所有配置属�?     */
    public Map<String, String> getAllProperties() {
        return new HashMap<>(properties);
    }
}