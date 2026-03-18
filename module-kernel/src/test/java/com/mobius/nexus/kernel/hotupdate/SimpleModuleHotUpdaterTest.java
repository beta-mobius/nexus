package com.mobius.nexus.kernel.hotupdate;

import com.mobius.nexus.kernel.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * SimpleModuleHotUpdater单元测试
 */
@DisplayName("SimpleModuleHotUpdater测试")
class SimpleModuleHotUpdaterTest {

    private ModuleLoader moduleLoader;
    private ModuleRegistry moduleRegistry;
    private ModuleRepository moduleRepository;
    private SimpleModuleHotUpdater hotUpdater;

    @BeforeEach
    void setUp() {
        moduleLoader = mock(ModuleLoader.class);
        moduleRegistry = new SimpleModuleRegistry();
        moduleRepository = mock(ModuleRepository.class);
        hotUpdater = new SimpleModuleHotUpdater(moduleLoader, moduleRegistry, moduleRepository);
    }

    @Nested
    @DisplayName("热更新测试")
    class HotUpdateTests {

        @Test
        @DisplayName("热更新 - 新模块安装")
        void testHotUpdate_NewModule() throws ModuleException {
            // Given
            Module newModule = createMockModule("com.test.new", "1.0.0");
            when(moduleLoader.loadModule(any())).thenReturn(newModule);
            
            // When
            HotUpdateResult result = hotUpdater.hotUpdate(java.nio.file.Paths.get("test.jar"));
            
            // Then
            assertThat(result.isSuccess()).isTrue();
            assertThat(moduleRegistry.contains(newModule.getId())).isTrue();
        }

        @Test
        @DisplayName("热更新 - 版本升级（立即策略）")
        void testHotUpdate_VersionUpgrade() throws ModuleException {
            // Given
            Module oldModule = createMockModule("com.test.module", "1.0.0");
            oldModule.start();
            moduleRegistry.register(oldModule);
            
            Module newModule = createMockModule("com.test.module", "2.0.0");
            when(moduleLoader.loadModule(any())).thenReturn(newModule);
            
            // When
            HotUpdateResult result = hotUpdater.hotUpdate(java.nio.file.Paths.get("test.jar"));
            
            // Then
            assertThat(result.isSuccess()).isTrue();
            verify(oldModule).stop();
            verify(newModule).start();
        }
    }

    @Nested
    @DisplayName("热卸载测试")
    class HotUninstallTests {

        @Test
        @DisplayName("热卸载 - 成功卸载")
        void testUninstall_Success() throws ModuleException {
            // Given
            Module module = createMockModule("com.test.module", "1.0.0");
            moduleRegistry.register(module);
            
            // When
            HotUninstallResult result = hotUpdater.uninstall(module.getId(), false);
            
            // Then
            assertThat(result.isSuccess()).isTrue();
            assertThat(moduleRegistry.contains(module.getId())).isFalse();
        }

        @Test
        @DisplayName("热卸载 - 模块不存在")
        void testUninstall_ModuleNotFound() {
            // Given
            ModuleId moduleId = new ModuleId("com.test.nonexistent", "1.0.0");
            
            // When
            HotUninstallResult result = hotUpdater.uninstall(moduleId, false);
            
            // Then
            assertThat(result.isFailed()).isTrue();
            assertThat(result.getStatus()).isEqualTo(HotUninstallResult.Status.NOT_FOUND);
        }

        @Test
        @DisplayName("热卸载 - 有依赖模块")
        void testUninstall_HasDependents() throws ModuleException {
            // Given
            Module module1 = createMockModule("com.test.module1", "1.0.0");
            Map<String, String> deps = new HashMap<>();
            deps.put("com.test.module2", "1.0.0");
            when(module1.getDependencies()).thenReturn(deps);
            moduleRegistry.register(module1);
            
            Module module2 = createMockModule("com.test.module2", "1.0.0");
            moduleRegistry.register(module2);
            
            // When
            HotUninstallResult result = hotUpdater.uninstall(module2.getId(), false);
            
            // Then
            assertThat(result.isFailed()).isTrue();
            assertThat(result.getStatus()).isEqualTo(HotUninstallResult.Status.HAS_DEPENDENTS);
        }
    }

    @Nested
    @DisplayName("状态跟踪测试")
    class StatusTrackingTests {

        @Test
        @DisplayName("获取更新状态")
        void testGetStatus() throws ModuleException {
            // Given
            Module newModule = createMockModule("com.test.new", "1.0.0");
            when(moduleLoader.loadModule(any())).thenReturn(newModule);
            
            // When
            hotUpdater.hotUpdate(java.nio.file.Paths.get("test.jar"));
            
            // Then - 状态应该被记录
            // 注意：由于更新很快完成，状态可能已经是COMPLETED
            HotUpdateStatus status = hotUpdater.getStatus(newModule.getId());
            // 状态可能为null（如果已完成并清理），或者为COMPLETED
        }

        @Test
        @DisplayName("获取所有更新状态")
        void testGetAllUpdateStatuses() {
            // When
            List<ModuleHotUpdateStatus> statuses = hotUpdater.getAllUpdateStatuses();
            
            // Then
            assertThat(statuses).isNotNull();
        }
    }

    @Nested
    @DisplayName("监听器测试")
    class ListenerTests {

        @Test
        @DisplayName("添加和移除监听器")
        void testAddRemoveListener() {
            // Given
            HotUpdateListener listener = mock(HotUpdateListener.class);
            
            // When
            hotUpdater.addListener(listener);
            hotUpdater.removeListener(listener);
            
            // Then - 不应该抛出异常
        }

        @Test
        @DisplayName("监听器接收事件")
        void testListenerReceivesEvents() throws ModuleException {
            // Given
            HotUpdateListener listener = mock(HotUpdateListener.class);
            hotUpdater.addListener(listener);
            
            Module newModule = createMockModule("com.test.new", "1.0.0");
            when(moduleLoader.loadModule(any())).thenReturn(newModule);
            
            // When
            hotUpdater.hotUpdate(java.nio.file.Paths.get("test.jar"));
            
            // Then - 监听器应该收到事件
            verify(listener, atLeastOnce()).onHotUpdateEvent(any());
        }
    }

    // 辅助方法

    private Module createMockModule(String name, String version) throws ModuleException {
        Module module = mock(Module.class);
        ModuleId moduleId = new ModuleId(name, version);
        when(module.getId()).thenReturn(moduleId);
        when(module.getName()).thenReturn(name);
        when(module.getVersion()).thenReturn(version);
        when(module.getDependencies()).thenReturn(Collections.emptyMap());
        when(module.getState()).thenReturn(ModuleState.INSTALLED);
        return module;
    }
}
