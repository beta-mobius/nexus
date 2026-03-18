package com.mobius.nexus.kernel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

/**
 * ModuleDependency单元测试
 * 
 * 测试模块依赖的解析和表示功能
 */
@DisplayName("ModuleDependency测试")
class ModuleDependencyTest {

    @Test
    @DisplayName("创建ModuleDependency - 基本属性")
    void testCreate_BasicProperties() {
        // When
        ModuleDependency dependency = new ModuleDependency(
            "com.example.user", 
            "[1.0,2.0)", 
            false, 
            true
        );
        
        // Then
        assertThat(dependency.getName()).isEqualTo("com.example.user");
        assertThat(dependency.getVersionRange()).isEqualTo("[1.0,2.0)");
        assertThat(dependency.isOptional()).isFalse();
        assertThat(dependency.isTransitive()).isTrue();
    }

    @ParameterizedTest
    @CsvSource({
        "com.example.user:1.0.0, com.example.user, 1.0.0",
        "com.mobius.nexus:[1.0,2.0), com.mobius.nexus, [1.0,2.0)",
        "test.module:0.0.1-SNAPSHOT, test.module, 0.0.1-SNAPSHOT"
    })
    @DisplayName("解析依赖字符串 - 标准格式")
    void testParse_StandardFormat(String input, String expectedName, String expectedVersion) {
        // When
        ModuleDependency dependency = ModuleDependency.parse(input);
        
        // Then
        assertThat(dependency.getName()).isEqualTo(expectedName);
        assertThat(dependency.getVersionRange()).isEqualTo(expectedVersion);
    }

    @Test
    @DisplayName("解析依赖字符串 - 可选依赖标记")
    void testParse_OptionalMarker() {
        // When
        ModuleDependency dependency = ModuleDependency.parse("com.example.user:[1.0,2.0)?");
        
        // Then
        assertThat(dependency.getName()).isEqualTo("com.example.user");
        assertThat(dependency.getVersionRange()).isEqualTo("[1.0,2.0)");
        assertThat(dependency.isOptional()).isTrue();
    }

    @Test
    @DisplayName("解析依赖字符串 - 无版本号")
    void testParse_NoVersion() {
        // When
        ModuleDependency dependency = ModuleDependency.parse("com.example.user");
        
        // Then
        assertThat(dependency.getName()).isEqualTo("com.example.user");
        // 默认版本可能是null或特定值
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", ":"})
    @DisplayName("解析依赖字符串 - 无效格式应抛出异常")
    void testParse_InvalidFormat(String invalidDependency) {
        // When & Then
        assertThatThrownBy(() -> ModuleDependency.parse(invalidDependency))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("依赖相等性测试")
    void testEquals() {
        // Given
        ModuleDependency dep1 = new ModuleDependency("com.example.user", "[1.0,2.0)", false, true);
        ModuleDependency dep2 = new ModuleDependency("com.example.user", "[1.0,2.0)", false, true);
        ModuleDependency dep3 = new ModuleDependency("com.example.user", "[2.0,3.0)", false, true);
        
        // Then
        assertThat(dep1).isEqualTo(dep2);
        assertThat(dep1.hashCode()).isEqualTo(dep2.hashCode());
        assertThat(dep1).isNotEqualTo(dep3);
    }

    @Test
    @DisplayName("toString格式正确")
    void testToString() {
        // Given
        ModuleDependency dependency = new ModuleDependency("com.example.user", "[1.0,2.0)", false, true);
        
        // When
        String str = dependency.toString();
        
        // Then
        assertThat(str).contains("com.example.user");
        assertThat(str).contains("[1.0,2.0)");
    }

    @Test
    @DisplayName("版本范围匹配 - 精确版本")
    void testVersionRangeMatching_ExactVersion() {
        // Given
        ModuleDependency dependency = new ModuleDependency("com.example.user", "1.0.0", false, false);
        
        // When & Then
        assertThat(dependency.matchesVersion("1.0.0")).isTrue();
        assertThat(dependency.matchesVersion("1.0.1")).isFalse();
        assertThat(dependency.matchesVersion("2.0.0")).isFalse();
    }

    @Test
    @DisplayName("版本范围匹配 - 范围版本")
    void testVersionRangeMatching_VersionRange() {
        // Given
        ModuleDependency dependency = new ModuleDependency("com.example.user", "[1.0,2.0)", false, false);
        
        // When & Then
        assertThat(dependency.matchesVersion("1.0.0")).isTrue();
        assertThat(dependency.matchesVersion("1.5.0")).isTrue();
        assertThat(dependency.matchesVersion("1.9.9")).isTrue();
        assertThat(dependency.matchesVersion("2.0.0")).isFalse(); // 不包含上限
        assertThat(dependency.matchesVersion("0.9.0")).isFalse(); // 低于下限
    }

    @Test
    @DisplayName("版本范围匹配 - 开放范围")
    void testVersionRangeMatching_OpenRange() {
        // Given
        ModuleDependency dependency = new ModuleDependency("com.example.user", "[1.0,)", false, false);
        
        // When & Then
        assertThat(dependency.matchesVersion("1.0.0")).isTrue();
        assertThat(dependency.matchesVersion("2.0.0")).isTrue();
        assertThat(dependency.matchesVersion("100.0.0")).isTrue();
        assertThat(dependency.matchesVersion("0.9.0")).isFalse();
    }
}
