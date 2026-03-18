package io.modular.framework.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 构造器参数值
 */
public class ConstructorArgumentValues {
    private final List<Object> indexedValues = new ArrayList<>();
    private final List<Object> genericValues = new ArrayList<>();
    
    /**
     * 添加索引参数值
     */
    public void addIndexedArgumentValue(int index, Object value) {
        ensureIndexCapacity(index);
        indexedValues.set(index, value);
    }
    
    /**
     * 添加通用参数值
     */
    public void addGenericArgumentValue(Object value) {
        genericValues.add(value);
    }
    
    /**
     * 获取参数值数量
     */
    public int getArgumentCount() {
        return Math.max(indexedValues.size(), genericValues.size());
    }
    
    /**
     * 获取索引参数值
     */
    public Object getIndexedArgumentValue(int index) {
        if (index < 0 || index >= indexedValues.size()) {
            return null;
        }
        return indexedValues.get(index);
    }
    
    /**
     * 获取通用参数值
     */
    public List<Object> getGenericArgumentValues() {
        return Collections.unmodifiableList(genericValues);
    }
    
    private void ensureIndexCapacity(int index) {
        while (indexedValues.size() <= index) {
            indexedValues.add(null);
        }
    }
}