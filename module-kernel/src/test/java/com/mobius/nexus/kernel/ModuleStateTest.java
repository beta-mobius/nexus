package com.mobius.nexus.kernel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

/**
 * ModuleState单元测试
 * 
 * 测试模块状态枚举及其转换逻辑
 */
@DisplayName("ModuleState测试")
class ModuleStateTest {

    @Test
    @DisplayName("模块状态枚举值存在")
    void testStateValues() {
        // When
        ModuleState[] states = ModuleState.values();
        
        // Then
        assertThat(states).contains(
            ModuleState.INSTALLED,
            ModuleState.RESOLVED,
            ModuleState.STARTING,
            ModuleState.ACTIVE,
            ModuleState.STOPPING,
            ModuleState.STOPPED,
            ModuleState.UNINSTALLED
        );
    }

    @Test
    @DisplayName("INSTALLED状态 - 初始状态")
    void testInstalledState() {
        // Given
        ModuleState state = ModuleState.INSTALLED;
        
        // Then
        assertThat(state.name()).isEqualTo("INSTALLED");
        assertThat(state.ordinal()).isEqualTo(0);
    }

    @Test
    @DisplayName("RESOLVED状态 - 依赖已解析")
    void testResolvedState() {
        // Given
        ModuleState state = ModuleState.RESOLVED;
        
        // Then
        assertThat(state.name()).isEqualTo("RESOLVED");
    }

    @Test
    @DisplayName("STARTING状态 - 正在启动")
    void testStartingState() {
        // Given
        ModuleState state = ModuleState.STARTING;
        
        // Then
        assertThat(state.name()).isEqualTo("STARTING");
    }

    @Test
    @DisplayName("ACTIVE状态 - 已激活")
    void testActiveState() {
        // Given
        ModuleState state = ModuleState.ACTIVE;
        
        // Then
        assertThat(state.name()).isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("STOPPING状态 - 正在停止")
    void testStoppingState() {
        // Given
        ModuleState state = ModuleState.STOPPING;
        
        // Then
        assertThat(state.name()).isEqualTo("STOPPING");
    }

    @Test
    @DisplayName("STOPPED状态 - 已停止")
    void testStoppedState() {
        // Given
        ModuleState state = ModuleState.STOPPED;
        
        // Then
        assertThat(state.name()).isEqualTo("STOPPED");
    }

    @Test
    @DisplayName("UNINSTALLED状态 - 已卸载")
    void testUninstalledState() {
        // Given
        ModuleState state = ModuleState.UNINSTALLED;
        
        // Then
        assertThat(state.name()).isEqualTo("UNINSTALLED");
    }

    @Test
    @DisplayName("状态转换顺序正确")
    void testStateTransitionOrder() {
        // Given
        ModuleState[] expectedOrder = {
            ModuleState.INSTALLED,
            ModuleState.RESOLVED,
            ModuleState.STARTING,
            ModuleState.ACTIVE,
            ModuleState.STOPPING,
            ModuleState.STOPPED,
            ModuleState.UNINSTALLED
        };
        
        // When
        ModuleState[] actualOrder = ModuleState.values();
        
        // Then
        assertThat(actualOrder).containsExactly(expectedOrder);
    }

    @Test
    @DisplayName("状态valueOf方法正确")
    void testValueOf() {
        // When & Then
        assertThat(ModuleState.valueOf("INSTALLED")).isEqualTo(ModuleState.INSTALLED);
        assertThat(ModuleState.valueOf("RESOLVED")).isEqualTo(ModuleState.RESOLVED);
        assertThat(ModuleState.valueOf("ACTIVE")).isEqualTo(ModuleState.ACTIVE);
    }

    @Test
    @DisplayName("无效状态名称应抛出异常")
    void testValueOf_InvalidName() {
        // When & Then
        assertThatThrownBy(() -> ModuleState.valueOf("INVALID_STATE"))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
