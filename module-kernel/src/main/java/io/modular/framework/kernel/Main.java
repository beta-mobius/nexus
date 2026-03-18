package io.modular.framework.kernel;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试程序，验证模块内核功能
 */
public class Main {
    
    public static void main(String[] args) throws Exception {
        System.out.println("=== 模块内核原型测试 ===");
        
        // 1. 创建版本测试
        testVersion();
        
        // 2. 创建模块ID测试
        testModuleId();
        
        // 3. 创建模块描述符测试
        testModuleDescriptor();
        
        // 4. 创建模块注册表和仓库
        testRegistryAndRepository();
        
        // 5. 测试模块加载（需要JAR文件）
        // testModuleLoading();
        
        System.out.println("\n=== 所有核心组件测试通过 ===");
    }
    
    private static void testVersion() {
        System.out.println("\n1. 版本测试");
        
        Version v1 = Version.of(1, 0, 0);
        Version v2 = Version.of(1, 1, 0);
        Version v3 = Version.of(2, 0, 0);
        
        System.out.println("v1: " + v1);
        System.out.println("v2: " + v2);
        System.out.println("v3: " + v3);
        
        System.out.println("v1 < v2: " + (v1.compareTo(v2) < 0));
        System.out.println("v2 < v3: " + (v2.compareTo(v3) < 0));
        System.out.println("v1.isCompatibleWith(v2): " + v1.isCompatibleWith(v2));
        System.out.println("v1.isCompatibleWith(v3): " + v1.isCompatibleWith(v3));
        
        // 解析测试
        Version parsed = Version.parse("1.2.3-alpha+build123");
        System.out.println("解析版本: " + parsed);
        System.out.println("是否稳定版本: " + parsed.isStable());
    }
    
    private static void testModuleId() {
        System.out.println("\n2. 模块ID测试");
        
        ModuleId id1 = ModuleId.of("example.module", "1.0.0");
        ModuleId id2 = ModuleId.of("example.module", "1.1.0");
        ModuleId id3 = ModuleId.of("another.module", "1.0.0");
        
        System.out.println("id1: " + id1);
        System.out.println("id2: " + id2);
        System.out.println("id3: " + id3);
        
        System.out.println("id1.isSameModule(id2): " + id1.isSameModule(id2));
        System.out.println("id1.isSameModule(id3): " + id1.isSameModule(id3));
        System.out.println("id1.isCompatibleWith(id2): " + id1.isCompatibleWith(id2));
        System.out.println("id1.isCompatibleWith(id3): " + id1.isCompatibleWith(id3));
    }
    
    private static void testModuleDescriptor() {
        System.out.println("\n3. 模块描述符测试");
        
        Map<String, String> dependencies = new HashMap<>();
        dependencies.put("core.module", "[1.0,2.0)");
        dependencies.put("util.module", "[1.5,)");
        
        ModuleDescriptor descriptor = ModuleDescriptor.builder()
            .moduleId("test.module", "1.0.0")
            .dependencies(dependencies)
            .exports(List.of("com.example.test"))
            .imports(List.of("com.example.core", "com.example.util"))
            .build();
        
        System.out.println("模块ID: " + descriptor.getModuleId());
        System.out.println("依赖: " + descriptor.getDependencies());
        System.out.println("导出: " + descriptor.getExports());
        System.out.println("导入: " + descriptor.getImports());
    }
    
    private static void testRegistryAndRepository() {
        System.out.println("\n4. 注册表和仓库测试");
        
        // 创建注册表
        ModuleRegistry registry = new SimpleModuleRegistry();
        
        // 创建类加载器
        ClassLoader classLoader = Main.class.getClassLoader();
        
        // 创建模块
        ModuleId moduleId = ModuleId.of("test.module", "1.0.0");
        Map<String, String> dependencies = new HashMap<>();
        Module module = new BaseModule(moduleId, dependencies, 
            List.of("com.example.test"), List.of(), classLoader);
        
        // 注册模块
        registry.register(module);
        System.out.println("注册模块: " + moduleId);
        System.out.println("模块数量: " + registry.size());
        
        // 获取模块
        registry.getModule(moduleId).ifPresent(m -> 
            System.out.println("获取模块: " + m.getId()));
        
        // 创建仓库
        SimpleModuleRepository repository = new SimpleModuleRepository(
            Paths.get("C:\\temp\\module-repository"));
        System.out.println("仓库根目录: " + repository.getClass().getSimpleName());
        System.out.println("仓库初始化完成");
    }
    
    private static void testModuleLoading() throws ModuleException {
        System.out.println("\n5. 模块加载测试");
        
        // 创建注册表和仓库
        ModuleRegistry registry = new SimpleModuleRegistry();
        SimpleModuleRepository repository = new SimpleModuleRepository(
            Paths.get("C:\\temp\\module-repository"));
        
        // 创建加载器
        ModuleLoader loader = new ModuleLoader(registry, repository);
        
        // 加载测试模块
        // TODO: 需要一个测试JAR文件
        // Module module = loader.loadModule(ModuleLocation.file("test-module.jar"));
        // System.out.println("加载模块: " + module.getId());
    }
}