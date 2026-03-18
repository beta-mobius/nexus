package com.mobius.nexus.kernel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.*;

/**
 * ModuleId单元测试
 * 
 * 测试模块标识符的创建、解析、比较等功能
 */
@DisplayName("ModuleId测试")
class ModuleIdTest {

    @Test
    @DisplayName("创建ModuleId - 使用名称和版本")
    void testCreateWithNameAndVersion() {
        // When
        ModuleId moduleId = new ModuleId("com.example.user", "1.0.0");
        
        // Then
        assertThat(moduleId.getName()).isEqualTo("com.example.user");
        assertThat(moduleId.getVersion()).isEqualTo("1.0.0");
        assertThat(moduleId.toString()).isEqualTo("com.example.user:1.0.0");
    }

    @Test
    @DisplayName("创建ModuleId - 使用默认版本")
    void testCreateWithDefaultVersion() {
        // When
        ModuleId moduleId = new ModuleId("com.example.product");
        
        // Then
        assertThat(moduleId.getName()).isEqualTo("com.example.product");
        assertThat(moduleId.getVersion()).isEqualTo("1.0.0");
    }

    @ParameterizedTest
    @CsvSource({
        "com.example.user, 1.0.0, com.example.user:1.0.0",
        "com.mobius.nexus, 2.5.3, com.mobius.nexus:2.5.3",
        "test.module, 0.0.1-SNAPSHOT, test.module:0.0.1-SNAPSHOT"
    })
    @DisplayName("ModuleId toString格式正确")
    void testToString(String name, String version, String expected) {
        // Given
        ModuleId moduleId = new ModuleId(name, version);
        
        // Then
        assertThat(moduleId.toString()).isEqualTo(expected);
    }

    @Test
    @DisplayName("ModuleId相等性测试 - 相同名称和版本")
    void testEquals_SameNameAndVersion() {
        // Given
        ModuleId id1 = new ModuleId("com.example.user", "1.0.0");
        ModuleId id2 = new ModuleId("com.example.user", "1.0.0");
        
        // Then
        assertThat(id1).isEqualTo(id2);
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }

    @Test
    @DisplayName("ModuleId相等性测试 - 不同名称")
    void testEquals_DifferentName() {
        // Given
        ModuleId id1 = new ModuleId("com.example.user", "1.0.0");
        ModuleId id2 = new ModuleId("com.example.product", "1.0.0");
        
        // Then
        assertThat(id1).isNotEqualTo(id2);
    }

    @Test
    @DisplayName("ModuleId相等性测试 - 不同版本")
    void testEquals_DifferentVersion() {
        // Given
        ModuleId id1 = new ModuleId("com.example.user", "1.0.0");
        ModuleId id2 = new ModuleId("com.example.user", "2.0.0");
        
        // Then
        assertThat(id1).isNotEqualTo(id2);
    }

    @Test
    @DisplayName("解析ModuleId字符串 - 标准格式")
    void testParse_StandardFormat() {
        // When
        ModuleId moduleId = ModuleId.parse("com.example.user:1.0.0");
        
        // Then
        assertThat(moduleId.getName()).isEqualTo("com.example.user");
        assertThat(moduleId.getVersion()).isEqualTo("1.0.0");
    }

    @Test
    @DisplayName("解析ModuleId字符串 - 无版本号")
    void testParse_NoVersion() {
        // When
        ModuleId moduleId = ModuleId.parse("com.example.user");
        
        // Then
        assertThat(moduleId.getName()).isEqualTo("com.example.user");
        assertThat(moduleId.getVersion()).isEqualTo("1.0.0");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", ":", ":1.0.0"})
    @DisplayName("解析ModuleId字符串 - 无效格式应抛出异常")
    void testParse_InvalidFormat(String invalidId) {
        // When & Then
        assertThatThrownBy(() -> ModuleId.parse(invalidId))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("ModuleId比较 - 版本排序")
    void testCompareTo_VersionOrder() {
        // Given
        ModuleId id1 = new ModuleId("com.example.user", "1.0.0");
        ModuleId id2 = new ModuleId("com.example.user", "1.1.0");
        ModuleId id3 = new ModuleId("com.example.user", "2.0.0");
        
        // Then
        assertThat(id1).isLessThan(id2);
        assertThat(id2).isLessThan(id3);
        assertThat(id1).isLessThan(id3);
    }

    @Test
    @DisplayName("ModuleId不可变 - 名称不可修改")
    void testImmutable_Name() {
        // Given
        ModuleId moduleId = new ModuleId("com.example.user", "1.0.0");
        
        // Then - ModuleId应该是不可变的，没有setter方法
        assertThat(moduleId.getName()).isEqualTo("com.example.user");
        // 如果尝试修改，编译应该失败（没有setName方法）
    }
}
