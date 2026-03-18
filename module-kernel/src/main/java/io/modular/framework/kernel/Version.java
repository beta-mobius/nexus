package io.modular.framework.kernel;

import java.util.Objects;

/**
 * 版本类，表示模块版本
 * 遵循语义化版本规范 (SemVer)：主版本号.次版本号.修订号
 */
public final class Version implements Comparable<Version> {
    private final int major;
    private final int minor;
    private final int patch;
    private final String preRelease;
    private final String buildMetadata;
    
    private Version(int major, int minor, int patch, String preRelease, String buildMetadata) {
        if (major < 0 || minor < 0 || patch < 0) {
            throw new IllegalArgumentException("Version numbers must be non-negative");
        }
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.preRelease = preRelease;
        this.buildMetadata = buildMetadata;
    }
    
    /**
     * 解析版本字符串
     * @param version 版本字符串，如 "1.0.0", "2.1.0-alpha", "3.0.0+build123"
     * @return Version对象
     */
    public static Version parse(String version) {
        if (version == null || version.trim().isEmpty()) {
            throw new IllegalArgumentException("Version string cannot be null or empty");
        }
        
        String trimmed = version.trim();
        
        // 分离构建元数据
        String buildMetadata = null;
        int plusIndex = trimmed.indexOf('+');
        if (plusIndex > 0) {
            buildMetadata = trimmed.substring(plusIndex + 1);
            trimmed = trimmed.substring(0, plusIndex);
        }
        
        // 分离预发布版本
        String preRelease = null;
        int hyphenIndex = trimmed.indexOf('-');
        if (hyphenIndex > 0) {
            preRelease = trimmed.substring(hyphenIndex + 1);
            trimmed = trimmed.substring(0, hyphenIndex);
        }
        
        // 解析主版本号.次版本号.修订号
        String[] parts = trimmed.split("\\.");
        if (parts.length < 1 || parts.length > 3) {
            throw new IllegalArgumentException("Invalid version format: " + version);
        }
        
        int major = Integer.parseInt(parts[0]);
        int minor = (parts.length > 1) ? Integer.parseInt(parts[1]) : 0;
        int patch = (parts.length > 2) ? Integer.parseInt(parts[2]) : 0;
        
        return new Version(major, minor, patch, preRelease, buildMetadata);
    }
    
    /**
     * 创建版本对象
     */
    public static Version of(int major, int minor, int patch) {
        return new Version(major, minor, patch, null, null);
    }
    
    /**
     * 创建带预发布版本的版本对象
     */
    public static Version of(int major, int minor, int patch, String preRelease) {
        return new Version(major, minor, patch, preRelease, null);
    }
    
    public int getMajor() {
        return major;
    }
    
    public int getMinor() {
        return minor;
    }
    
    public int getPatch() {
        return patch;
    }
    
    public String getPreRelease() {
        return preRelease;
    }
    
    public String getBuildMetadata() {
        return buildMetadata;
    }
    
    /**
     * 检查是否兼容（主版本号相同）
     */
    public boolean isCompatibleWith(Version other) {
        return this.major == other.major;
    }
    
    /**
     * 检查是否是稳定版本（无预发布标识）
     */
    public boolean isStable() {
        return preRelease == null || preRelease.isEmpty();
    }
    
    @Override
    public int compareTo(Version other) {
        if (this.major != other.major) {
            return Integer.compare(this.major, other.major);
        }
        if (this.minor != other.minor) {
            return Integer.compare(this.minor, other.minor);
        }
        if (this.patch != other.patch) {
            return Integer.compare(this.patch, other.patch);
        }
        
        // 预发布版本比较：有预发布标识的版本小于无预发布标识的版本
        if (this.preRelease == null && other.preRelease == null) {
            return 0;
        }
        if (this.preRelease == null) {
            return 1; // 稳定版本大于预发布版本
        }
        if (other.preRelease == null) {
            return -1; // 预发布版本小于稳定版本
        }
        
        return this.preRelease.compareTo(other.preRelease);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Version version = (Version) o;
        return major == version.major &&
               minor == version.minor &&
               patch == version.patch &&
               Objects.equals(preRelease, version.preRelease);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch, preRelease);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(major).append('.').append(minor).append('.').append(patch);
        if (preRelease != null && !preRelease.isEmpty()) {
            sb.append('-').append(preRelease);
        }
        if (buildMetadata != null && !buildMetadata.isEmpty()) {
            sb.append('+').append(buildMetadata);
        }
        return sb.toString();
    }
}