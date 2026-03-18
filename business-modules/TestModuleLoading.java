package com.example.test;

import io.modular.framework.kernel.*;
import io.modular.framework.api.BeanContainer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模块加载测试程序
 * 演示如何创建、加载和使用模块
 */
public class TestModuleLoading {
    
    public static void main(String[] args) throws Exception {
        System.out.println("=== 模块框架测试 ===");
        
        // 1. 创建模块描述符
        ModuleDescriptor descriptor = createUserModuleDescriptor();
        System.out.println("创建模块描述符: " + descriptor.getModuleId());
        
        // 2. 创建模块注册表和仓库
        ModuleRegistry registry = new SimpleModuleRegistry();
        SimpleModuleRepository repository = new SimpleModuleRepository(
            java.nio.file.Paths.get("C:\\temp\\module-repository"));
        
        // 3. 创建模块加载器
        ModuleLoader loader = new ModuleLoader(registry, repository);
        
        // 4. 模拟模块加载（简化，不实际加载JAR）
        System.out.println("\n模拟模块加载流程:");
        System.out.println("1. 解析模块依赖...");
        System.out.println("2. 创建类加载器...");
        System.out.println("3. 实例化模块...");
        
        // 5. 创建模拟模块
        ClassLoader classLoader = TestModuleLoading.class.getClassLoader();
        Module module = createMockUserModule(descriptor, classLoader);
        
        // 6. 注册模块
        registry.register(module);
        System.out.println("模块已注册: " + module.getId());
        
        // 7. 启动模块
        System.out.println("\n启动模块...");
        module.start();
        System.out.println("模块状态: " + module.getState());
        
        // 8. 注册服务
        System.out.println("\n注册用户服务...");
        UserService userService = new UserServiceImpl();
        module.registerService(userService, UserService.class);
        
        // 9. 获取服务
        System.out.println("\n获取用户服务...");
        List<UserService> services = module.getServices(UserService.class);
        if (!services.isEmpty()) {
            UserService service = services.get(0);
            
            // 测试服务功能
            System.out.println("测试用户服务:");
            User user = new User(null, "张三", "zhangsan@example.com", 25);
            String userId = service.createUser(user);
            System.out.println("创建用户: " + userId);
            
            User retrieved = service.getUser(userId);
            System.out.println("获取用户: " + retrieved);
        }
        
        // 10. 停止模块
        System.out.println("\n停止模块...");
        module.stop();
        System.out.println("模块状态: " + module.getState());
        
        System.out.println("\n=== 测试完成 ===");
    }
    
    private static ModuleDescriptor createUserModuleDescriptor() {
        Map<String, String> dependencies = new HashMap<>();
        // 依赖声明
        dependencies.put("core.module", "[1.0,2.0)");
        dependencies.put("util.module", "[1.5,)");
        
        return ModuleDescriptor.builder()
            .moduleId("com.example.user", "1.0.0")
            .dependencies(dependencies)
            .exports(java.util.List.of("com.example.user"))
            .imports(java.util.List.of("com.example.core", "com.example.util"))
            .build();
    }
    
    private static Module createMockUserModule(ModuleDescriptor descriptor, ClassLoader classLoader) {
        return new BaseModule(
            descriptor.getModuleId(),
            descriptor.getDependencies(),
            descriptor.getExports(),
            descriptor.getImports(),
            classLoader
        );
    }
    
    // 简化版的User类
    static class User {
        private String userId;
        private String username;
        private String email;
        private int age;
        
        public User(String userId, String username, String email, int age) {
            this.userId = userId;
            this.username = username;
            this.email = email;
            this.age = age;
        }
        
        @Override
        public String toString() {
            return "User{userId='" + userId + "', username='" + username + 
                   "', email='" + email + "', age=" + age + "}";
        }
    }
    
    // 简化版的UserService接口
    interface UserService {
        User getUser(String userId);
        String createUser(User user);
        void updateUser(User user);
        void deleteUser(String userId);
    }
    
    // 简化版的UserService实现
    static class UserServiceImpl implements UserService {
        private final java.util.Map<String, User> userStore = new java.util.HashMap<>();
        private int nextUserId = 1;
        
        @Override
        public User getUser(String userId) {
            return userStore.get(userId);
        }
        
        @Override
        public String createUser(User user) {
            String userId = "user-" + nextUserId++;
            return userId;
        }
        
        @Override
        public void updateUser(User user) {
            // 简化实现
        }
        
        @Override
        public void deleteUser(String userId) {
            userStore.remove(userId);
        }
    }
}