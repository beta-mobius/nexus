package com.example.user;

import com.mobius.nexus.kernel.ModuleActivator;
import com.mobius.nexus.kernel.ModuleContext;

/**
 * 用户模块激活器
 */
public class UserModule implements ModuleActivator {
    
    @Override
    public void start(ModuleContext context) {
        System.out.println("用户模块启动: " + context.getModule().getName());
        
        // 注册用户服务
        UserService userService = new UserServiceImpl();
        context.registerService(userService, UserService.class);
        
        System.out.println("用户服务已注册");
    }
    
    @Override
    public void stop(ModuleContext context) {
        System.out.println("用户模块停止: " + context.getModule().getName());
    }
}