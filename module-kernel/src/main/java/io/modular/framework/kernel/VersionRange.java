package io.modular.framework.kernel;

import java.util.Objects;

/**
 * 版本范围
 * 表示一个版本区间，如 "[1.0,2.0)" 表示 1.0 ≤ version < 2.0
 */
public class VersionRange {
    private final Version minVersion;
    private final Version maxVersion;
    private final boolean minInclusive;
    private final boolean maxInclusive;
    
    private VersionRange(Version minVersion, Version maxVersion, 
                        boolean minInclusive, boolean maxInclusive) {
        this.minVersion = minVersion;
        this.maxVersion = maxVersion;
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
    }
    
    /**
     * 解析版本范围字符串
     * 支持格式："[1.0,2.0)", "(1.0,2.0]", "[1.0,)", "(,2.0]", "1.0"
     */
    public static VersionRange parse(String range) {
        if (range == null || range.trim().isEmpty()) {
            throw new IllegalArgumentException("Version range cannot be null or empty");
        }
        
        String trimmed = range.trim();
        
        // 如果是单个版本，转换为 [version,)
        if (!trimmed.startsWith("[") && !trimmed.startsWith("(")) {
            Version version = Version.parse(trimmed);
            return new VersionRange(version, null, true, false);
        }
        
        // 解析区间
        boolean minInclusive = trimmed.startsWith("[");
        boolean maxInclusive = trimmed.endsWith("]");
        
        // 去除括号
        String content = trimmed.substring(1, trimmed.length() - 1);
        String[] parts = content.split(",");
        
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid version range format: " + trimmed);
        }
        
        Version minVersion = parts[0].trim().isEmpty() ? null : Version.parse(parts[0].trim());
        Version maxVersion = parts[1].trim().isEmpty() ? null : Version.parse(parts[1].trim());
        
        return new VersionRange(minVersion, maxVersion, minInclusive, maxInclusive);
    }
    
    /**
     * 检查版本是否在范围内
     */
    public boolean contains(Version version) {
        if (minVersion != null) {
            int cmp = version.compareTo(minVersion);
            if (cmp < 0 || (cmp == 0 && !minInclusive)) {
                return false;
            }
        }
        
        if (maxVersion != null) {
            int cmp = version.compareTo(maxVersion);
            if (cmp > 0 || (cmp == 0 && !maxInclusive)) {
                return false;
            }
        }
        
        return true;
    }
    
    public Version getMinVersion() {
        return minVersion;
    }
    
    public Version getMaxVersion() {
        return maxVersion;
    }
    
    public boolean isMinInclusive() {
        return minInclusive;
    }
    
    public boolean isMaxInclusive() {
        return maxInclusive;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VersionRange that = (VersionRange) o;
        return minInclusive == that.minInclusive &&
               maxInclusive == that.maxInclusive &&
               Objects.equals(minVersion, that.minVersion) &&
               Objects.equals(maxVersion, that.maxVersion);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(minVersion, maxVersion, minInclusive, maxInclusive);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(minInclusive ? '[' : '(');
        sb.append(minVersion != null ? minVersion.toString() : "");
        sb.append(',');
        sb.append(maxVersion != null ? maxVersion.toString() : "");
        sb.append(maxInclusive ? ']' : ')');
        return sb.toString();
    }
}