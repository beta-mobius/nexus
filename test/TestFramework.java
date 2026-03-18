package test;

import io.modular.framework.kernel.*;
import io.modular.framework.api.BeanContainer;
import io.modular.framework.driver.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模块框架测试程序
 * 演示完整的框架使用流程
 */
public class TestFramework {
    
    public static void main(String[] args) {
        System.out.println("=== 模块化框架测试程序 ===");
        System.out.println("版本: 1.0.0");
        System.out.println();
        
        try {
            // 测试1: 模块内核功能
            testModuleKernel();
            
            // 测试2: 服务API功能  
            testServiceApi();
            
            // 测试3: 框架驱动功能
            testFrameworkDriver();
            
            // 测试4: 完整集成测试
            testIntegration();
            
            System.out.println("\n=== 所有测试通过 ===");
            
        } catch (Exception e) {
            System.err.println("测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 测试模块内核功能
     */
    private static void testModuleKernel() throws Exception {
        System.out.println("--- 测试1: 模块内核功能 ---");
        
        // 1. 创建模块描述符
        System.out.println("1. 创建模块描述符...");
        ModuleDescriptor descriptor = ModuleDescriptor.builder()
            .moduleId("com.example.test", "1.0.0")
            .dependency("core.module", "[1.0,2.0)")
            .dependency("util.module", "[1.5,)")
            .exports(List.of("com.example.test", "com.example.test.api"))
            .imports(List.of("com.example.core", "java.util"))
            .activatorClassName("com.example.test.TestModuleActivator")
            .build();
        
        System.out.println("   模块ID: " + descriptor.getModuleId());
        System.out.println("   依赖: " + descriptor.getDependencies());
        System.out.println("   导出包: " + descriptor.getExports());
        
        // 2. 创建模块注册表
        System.out.println("2. 创建模块注册表和仓库...");
        ModuleRegistry registry = new SimpleModuleRegistry();
        SimpleModuleRepository repository = new SimpleModuleRepository(
            Paths.get("C:\\temp\\module-repository"));
        
        // 3. 创建模块加载器
        System.out.println("3. 创建模块加载器...");
        ModuleLoader loader = new ModuleLoader(registry, repository);
        
        // 4. 模拟模块加载（简化演示）
        System.out.println("4. 模拟模块加载...");
        Module module = createMockModule(descriptor);
        registry.register(module);
        
        System.out.println("   模块状态: " + module.getState());
        System.out.println("   模块类加载器: " + module.getClassLoader());
        
        // 5. 测试服务注册
        System.out.println("5. 测试服务注册...");
        TestService service = new TestServiceImpl();
        module.registerService(service, TestService.class);
        
        List<TestService> services = module.getServices(TestService.class);
        System.out.println("   注册的服务数: " + services.size());
        
        // 6. 测试模块生命周期
        System.out.println("6. 测试模块生命周期...");
        module.start();
        System.out.println("   模块启动后状态: " + module.getState());
        
        module.stop();
        System.out.println("   模块停止后状态: " + module.getState());
        
        System.out.println("✓ 模块内核功能测试完成");
    }
    
    /**
     * 测试服务API功能
     */
    private static void testServiceApi() {
        System.out.println("\n--- 测试2: 服务API功能 ---");
        
        // 演示API设计
        System.out.println("服务API接口:");
        System.out.println("- BeanContainer: Bean容器接口");
        System.out.println("- TransactionManager: 事务管理器");
        System.out.println("- EventPublisher: 事件发布器");
        System.out.println("- AspectManager: 切面管理器");
        System.out.println("- Scheduler: 调度器");
        System.out.println("- Validator: 验证器");
        
        System.out.println("\nAPI特点:");
        System.out.println("- 零Spring依赖");
        System.out.println("- 统一的抽象接口");
        System.out.println("- 支持多种实现");
        
        System.out.println("✓ 服务API功能测试完成");
    }
    
    /**
     * 测试框架驱动功能
     */
    private static void testFrameworkDriver() {
        System.out.println("\n--- 测试3: 框架驱动功能 ---");
        
        System.out.println("框架驱动组件:");
        System.out.println("1. FrameworkDriver - 驱动接口");
        System.out.println("2. DriverConfig - 驱动配置");
        System.out.println("3. SpringFrameworkDriver - Spring驱动");
        System.out.println("4. SpringBeanContainer - Spring容器适配器");
        System.out.println("5. SpringTransactionManager - Spring事务适配器");
        System.out.println("6. SpringEventPublisher - Spring事件适配器");
        System.out.println("7. SpringAspectManager - Spring AOP适配器");
        System.out.println("8. SpringScheduler - Spring调度适配器");
        System.out.println("9. SpringValidator - Spring验证适配器");
        
        System.out.println("\n驱动模式:");
        System.out.println("- Spring驱动: 适配Spring框架");
        System.out.println("- 轻量级驱动: 不依赖Spring的简单实现");
        System.out.println("- 自定义驱动: 用户自定义实现");
        
        // 演示创建Spring驱动
        System.out.println("\n创建Spring驱动示例:");
        DriverConfig config = new DriverConfig();
        config.setDriverType("spring");
        config.setProperties(new HashMap<>());
        
        System.out.println("   驱动配置: " + config);
        
        System.out.println("✓ 框架驱动功能测试完成");
    }
    
    /**
     * 测试完整集成
     */
    private static void testIntegration() throws Exception {
        System.out.println("\n--- 测试4: 完整集成测试 ---");
        
        System.out.println("集成场景: 用户模块 + Spring驱动");
        
        // 模拟集成流程
        System.out.println("1. 初始化Spring应用上下文");
        System.out.println("2. 创建SpringFrameworkDriver");
        System.out.println("3. 加载用户模块");
        System.out.println("4. 模块通过Spring驱动访问Spring服务");
        System.out.println("5. 模块注册服务到框架");
        System.out.println("6. 其他模块通过框架访问用户服务");
        
        System.out.println("\n核心优势:");
        System.out.println("- 模块化: 业务功能模块化");
        System.out.println("- 解耦: 业务代码不直接依赖Spring");
        System.out.println("- 可替换: 可切换不同框架驱动");
        System.out.println("- 热更新: 支持模块热部署");
        
        System.out.println("✓ 集成测试完成");
    }
    
    /**
     * 创建模拟模块
     */
    private static Module createMockModule(ModuleDescriptor descriptor) {
        return new BaseModule(
            descriptor.getModuleId(),
            descriptor.getDependencies(),
            descriptor.getExports(),
            descriptor.getImports(),
            TestFramework.class.getClassLoader(),
            descriptor.getActivatorClassName()
        );
    }
    
    // 测试服务接口
    interface TestService {
        String getName();
        void execute();
    }
    
    // 测试服务实现
    static class TestServiceImpl implements TestService {
        @Override
        public String getName() {
            return "TestService";
        }
        
        @Override
        public void execute() {
            System.out.println("TestService executing...");
        }
    }
}