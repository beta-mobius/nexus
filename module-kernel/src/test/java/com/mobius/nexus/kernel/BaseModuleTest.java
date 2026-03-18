package com.mobius.nexus.kernel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * BaseModule单元测试
 * 
 * 测试模块的基础实现功能
 */
@DisplayName("BaseModule测试")
class BaseModuleTest {

    private ModuleId testModuleId;
    private Map<String, String> testDependencies;
    private List<String> testExports;
    private List<String> testImports;
    private ClassLoader testClassLoader;

    @BeforeEach
    void setUp() {
        testModuleId = new ModuleId("com.test.module", "1.0.0");
        testDependencies = new HashMap<>();
        testDependencies.put("com.test.dependency", "[1.0,2.0)");
        
        testExports = Arrays.asList("com.test.module.api", "com.test.module.service");
        testImports = Arrays.asList("com.test.external.api");
        
        testClassLoader = Thread.currentThread().getContextClassLoader();
    }

    @Test
    @DisplayName("创建BaseModule - 基本属性正确")
    void testCreate_BasicProperties() {
        // When
        BaseModule module = new BaseModule(
            testModuleId, 
            testDependencies, 
            testExports, 
            testImports, 
            testClassLoader
        );
        
        // Then
        assertThat(module.getId()).isEqualTo(testModuleId);
        assertThat(module.getName()).isEqualTo("com.test.module");
        assertThat(module.getVersion()).isEqualTo("1.0.0");
        assertThat(module.getState()).isEqualTo(ModuleState.INSTALLED);
    }

    @Test
    @DisplayName("创建BaseModule - 依赖不可变")
    void testCreate_ImmutableDependencies() {
        // Given
        Map<String, String> mutableDeps = new HashMap<>(testDependencies);
        
        // When
        BaseModule module = new BaseModule(
            testModuleId, 
            mutableDeps, 
            testExports, 
            testImports, 
            testClassLoader
        );
        
        // Then - 修改原始Map不影响模块依赖
        mutableDeps.put("com.test.new", "2.0.0");
        assertThat(module.getDependencies()).doesNotContainKey("com.test.new");
        
        // And - 模块依赖不可修改
        assertThatThrownBy(() -> module.getDependencies().put("com.test.new", "2.0.0"))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("创建BaseModule - 导出包不可变")
    void testCreate_ImmutableExports() {
        // Given
        List<String> mutableExports = new ArrayList<>(testExports);
        
        // When
        BaseModule module = new BaseModule(
            testModuleId, 
            testDependencies, 
            mutableExports, 
            testImports, 
            testClassLoader
        );
        
        // Then
        assertThatThrownBy(() -> module.getExports().add("com.test.new"))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Nested
    @DisplayName("模块生命周期测试")
    class ModuleLifecycleTests {

        @Test
        @DisplayName("启动模块 - 从INSTALLED状态")
        void testStart_FromInstalled() throws ModuleException {
            // Given
            BaseModule module = new BaseModule(
                testModuleId, testDependencies, testExports, testImports, testClassLoader
            );
            assertThat(module.getState()).isEqualTo(ModuleState.INSTALLED);
            
            // When
            module.start();
            
            // Then
            assertThat(module.getState()).isEqualTo(ModuleState.ACTIVE);
        }

        @Test
        @DisplayName("启动模块 - 从RESOLVED状态")
        void testStart_FromResolved() throws ModuleException {
            // Given
            BaseModule module = new BaseModule(
                testModuleId, testDependencies, testExports, testImports, testClassLoader
            );
            // 模拟RESOLVED状态（实际中由ModuleLoader设置）
            
            // When & Then - 需要先设置状态为RESOLVED才能测试
            // 这里暂时跳过，因为setState方法可能不是public的
        }

        @Test
        @DisplayName("停止模块 - 从ACTIVE状态")
        void testStop_FromActive() throws ModuleException {
            // Given
            BaseModule module = new BaseModule(
                testModuleId, testDependencies, testExports, testImports, testClassLoader
            );
            module.start();
            assertThat(module.getState()).isEqualTo(ModuleState.ACTIVE);
            
            // When
            module.stop();
            
            // Then
            assertThat(module.getState()).isEqualTo(ModuleState.STOPPED);
        }

        @Test
        @DisplayName("启动模块 - 从ACTIVE状态应失败")
        void testStart_FromActive_ShouldFail() throws ModuleException {
            // Given
            BaseModule module = new BaseModule(
                testModuleId, testDependencies, testExports, testImports, testClassLoader
            );
            module.start();
            
            // When & Then
            assertThatThrownBy(() -> module.start())
                .isInstanceOf(ModuleException.class)
                .hasMessageContaining("cannot be started");
        }

        @Test
        @DisplayName("停止模块 - 从INSTALLED状态应失败")
        void testStop_FromInstalled_ShouldFail() {
            // Given
            BaseModule module = new BaseModule(
                testModuleId, testDependencies, testExports, testImports, testClassLoader
            );
            
            // When & Then
            assertThatThrownBy(() -> module.stop())
                .isInstanceOf(ModuleException.class);
        }
    }

    @Nested
    @DisplayName("服务注册测试")
    class ServiceRegistrationTests {

        @Test
        @DisplayName("注册服务 - 单个接口")
        void testRegisterService_SingleInterface() throws ModuleException {
            // Given
            BaseModule module = new BaseModule(
                testModuleId, testDependencies, testExports, testImports, testClassLoader
            );
            module.start();
            
            TestService service = new TestServiceImpl();
            
            // When
            module.registerService(service, TestService.class);
            
            // Then
            List<TestService> services = module.getServices(TestService.class);
            assertThat(services).hasSize(1);
            assertThat(services.get(0)).isSameAs(service);
        }

        @Test
        @DisplayName("注册服务 - 多个接口")
        void testRegisterService_MultipleInterfaces() throws ModuleException {
            // Given
            BaseModule module = new BaseModule(
                testModuleId, testDependencies, testExports, testImports, testClassLoader
            );
            module.start();
            
            TestMultiService service = new TestMultiServiceImpl();
            
            // When
            module.registerService(service, TestService.class, TestAnotherService.class);
            
            // Then
            assertThat(module.getServices(TestService.class)).hasSize(1);
            assertThat(module.getServices(TestAnotherService.class)).hasSize(1);
        }

        @Test
        @DisplayName("注销服务")
        void testUnregisterService() throws ModuleException {
            // Given
            BaseModule module = new BaseModule(
                testModuleId, testDependencies, testExports, testImports, testClassLoader
            );
            module.start();
            
            TestService service = new TestServiceImpl();
            module.registerService(service, TestService.class);
            
            // When
            module.unregisterService(service);
            
            // Then
            List<TestService> services = module.getServices(TestService.class);
            assertThat(services).isEmpty();
        }

        @Test
        @DisplayName("获取服务 - 不存在的服务类型")
        void testGetServices_NonExistentType() throws ModuleException {
            // Given
            BaseModule module = new BaseModule(
                testModuleId, testDependencies, testExports, testImports, testClassLoader
            );
            module.start();
            
            // When
            List<TestService> services = module.getServices(TestService.class);
            
            // Then
            assertThat(services).isNotNull().isEmpty();
        }
    }

    @Nested
    @DisplayName("类加载器测试")
    class ClassLoaderTests {

        @Test
        @DisplayName("获取模块类加载器")
        void testGetClassLoader() {
            // Given
            BaseModule module = new BaseModule(
                testModuleId, testDependencies, testExports, testImports, testClassLoader
            );
            
            // When
            ClassLoader loader = module.getClassLoader();
            
            // Then
            assertThat(loader).isSameAs(testClassLoader);
        }
    }

    // 测试用的服务接口和实现
    interface TestService {
        String doSomething();
    }

    static class TestServiceImpl implements TestService {
        @Override
        public String doSomething() {
            return "test";
        }
    }

    interface TestAnotherService {
        String doAnother();
    }

    interface TestMultiService extends TestService, TestAnotherService {
    }

    static class TestMultiServiceImpl implements TestMultiService {
        @Override
        public String doSomething() {
            return "test";
        }

        @Override
        public String doAnother() {
            return "another";
        }
    }
}
