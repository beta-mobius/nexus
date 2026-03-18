package io.modular.framework.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 条件事件监听注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventListener {
    
    /**
     * 监听的事件类型
     */
    Class<?>[] value() default {};
    
    /**
     * 监听条件（SpEL表达式）
     */
    String condition() default "";
    
    /**
     * 是否异步执行
     */
    boolean async() default false;
}