package com.example.order;

import io.modular.framework.kernel.ModuleActivator;
import io.modular.framework.kernel.ModuleContext;

/**
 * 订单模块激活器
 */
public class OrderModule implements ModuleActivator {
    
    @Override
    public void start(ModuleContext context) {
        System.out.println("订单模块启动: " + context.getModule().getName());
        
        // 注册订单服务
        OrderService orderService = new OrderServiceImpl();
        context.registerService(orderService, OrderService.class);
        
        System.out.println("订单服务已注册");
        
        // 尝试获取用户服务（可选依赖）
        try {
            com.example.user.UserService userService = 
                context.getService(com.example.user.UserService.class);
            if (userService != null) {
                System.out.println("用户服务可用，订单模块可以集成用户功能");
            }
        } catch (Exception e) {
            System.out.println("用户服务不可用，订单模块独立运行");
        }
    }
    
    @Override
    public void stop(ModuleContext context) {
        System.out.println("订单模块停止: " + context.getModule().getName());
    }
}