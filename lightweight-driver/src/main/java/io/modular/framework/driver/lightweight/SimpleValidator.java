package io.modular.framework.driver.lightweight;

import io.modular.framework.api.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 简单验证器实现
 * 支持基本的字段验证和自定义验证规则
 */
public class SimpleValidator implements Validator {
    
    private final Map<Class<?>, List<ValidationRule<?>>> validationRules = new ConcurrentHashMap<>();
    
    @Override
    public boolean supports(Class<?> clazz) {
        // 支持所有类
        return true;
    }
    
    @Override
    public void validate(Object target, Errors errors) {
        if (target == null) {
            errors.addError(new ObjectError("target", "Target object cannot be null"));
            return;
        }
        
        Class<?> clazz = target.getClass();
        
        // 应用类级别的验证规则
        List<ValidationRule<?>> classRules = validationRules.get(clazz);
        if (classRules != null) {
            for (ValidationRule<?> rule : classRules) {
                if (rule instanceof ClassValidationRule) {
                    String errorMessage = ((ClassValidationRule<?>) rule).validate(target);
                    if (errorMessage != null) {
                        errors.addError(new ObjectError(clazz.getSimpleName(), errorMessage));
                    }
                }
            }
        }
        
        // 验证字段
        for (Field field : getAllFields(clazz)) {
            field.setAccessible(true);
            
            try {
                Object value = field.get(target);
                
                // 检查字段注解
                for (Annotation annotation : field.getAnnotations()) {
                    String error = validateAnnotation(annotation, field.getName(), value);
                    if (error != null) {
                        errors.addError(new FieldError(
                            field.getName(),
                            value,
                            error
                        ));
                    }
                }
                
                // 应用字段级别的验证规则
                List<ValidationRule<?>> fieldRules = validationRules.get(field.getType());
                if (fieldRules != null) {
                    for (ValidationRule<?> rule : fieldRules) {
                        if (rule instanceof FieldValidationRule) {
                            String errorMessage = ((FieldValidationRule<?>) rule).validate(field.getName(), value);
                            if (errorMessage != null) {
                                errors.addError(new FieldError(field.getName(), value, errorMessage));
                            }
                        }
                    }
                }
                
            } catch (IllegalAccessException e) {
                // 忽略无法访问的字段
            }
        }
    }
    
    @Override
    public Set<ConstraintViolation<Object>> validate(Object target) {
        SimpleErrors errors = new SimpleErrors(target);
        validate(target, errors);
        return errors.getViolations();
    }
    
    /**
     * 注册验证规则
     */
    public <T> void registerRule(Class<T> targetType, ValidationRule<T> rule) {
        validationRules.computeIfAbsent(targetType, k -> new ArrayList<>())
                      .add(rule);
    }
    
    /**
     * 验证注解
     */
    private String validateAnnotation(Annotation annotation, String fieldName, Object value) {
        // 简单实现，支持常见注解
        
        String annotationName = annotation.annotationType().getSimpleName();
        
        if ("NotNull".equals(annotationName) || "NonNull".equals(annotationName)) {
            if (value == null) {
                return fieldName + " must not be null";
            }
        } else if ("NotEmpty".equals(annotationName)) {
            if (value == null || value.toString().trim().isEmpty()) {
                return fieldName + " must not be empty";
            }
        } else if ("Size".equals(annotationName)) {
            try {
                // 尝试获取注解属性（简化实现）
                // 实际中需要通过反射读取min/max值
                if (value != null) {
                    int length = value.toString().length();
                    if (length < 1 || length > 255) {
                        return fieldName + " size must be between 1 and 255";
                    }
                }
            } catch (Exception e) {
                // 忽略注解解析错误
            }
        } else if ("Email".equals(annotationName)) {
            if (value != null && !isValidEmail(value.toString())) {
                return fieldName + " must be a valid email address";
            }
        }
        
        return null;
    }
    
    /**
     * 简单的邮箱验证
     */
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    /**
     * 获取类及其父类的所有字段
     */
    private List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }
    
    /**
     * 验证规则接口
     */
    public interface ValidationRule<T> {
    }
    
    /**
     * 类级别验证规则
     */
    public interface ClassValidationRule<T> extends ValidationRule<T> {
        String validate(T target);
    }
    
    /**
     * 字段级别验证规则
     */
    public interface FieldValidationRule<T> extends ValidationRule<T> {
        String validate(String fieldName, T value);
    }
    
    /**
     * 简单的错误收集器
     */
    private static class SimpleErrors implements Errors {
        private final Object target;
        private final List<ObjectError> objectErrors = new ArrayList<>();
        private final List<FieldError> fieldErrors = new ArrayList<>();
        
        SimpleErrors(Object target) {
            this.target = target;
        }
        
        @Override
        public void addError(ObjectError error) {
            objectErrors.add(error);
        }
        
        @Override
        public void addError(FieldError error) {
            fieldErrors.add(error);
        }
        
        @Override
        public boolean hasErrors() {
            return !objectErrors.isEmpty() || !fieldErrors.isEmpty();
        }
        
        @Override
        public List<ObjectError> getObjectErrors() {
            return Collections.unmodifiableList(objectErrors);
        }
        
        @Override
        public List<FieldError> getFieldErrors() {
            return Collections.unmodifiableList(fieldErrors);
        }
        
        Set<ConstraintViolation<Object>> getViolations() {
            Set<ConstraintViolation<Object>> violations = new HashSet<>();
            
            for (ObjectError error : objectErrors) {
                violations.add(new SimpleConstraintViolation<>(target, error));
            }
            
            for (FieldError error : fieldErrors) {
                violations.add(new SimpleConstraintViolation<>(target, error));
            }
            
            return violations;
        }
    }
    
    /**
     * 简单的约束违反实现
     */
    private static class SimpleConstraintViolation<T> implements ConstraintViolation<T> {
        private final T rootBean;
        private final String message;
        private final String propertyPath;
        private final Object invalidValue;
        
        SimpleConstraintViolation(T rootBean, ObjectError error) {
            this.rootBean = rootBean;
            this.message = error.getMessage();
            this.propertyPath = error.getObjectName();
            this.invalidValue = null;
        }
        
        SimpleConstraintViolation(T rootBean, FieldError error) {
            this.rootBean = rootBean;
            this.message = error.getMessage();
            this.propertyPath = error.getField();
            this.invalidValue = error.getRejectedValue();
        }
        
        @Override
        public String getMessage() {
            return message;
        }
        
        @Override
        public String getPropertyPath() {
            return propertyPath;
        }
        
        @Override
        public Object getInvalidValue() {
            return invalidValue;
        }
        
        @Override
        public Class<T> getRootBeanClass() {
            return (Class<T>) rootBean.getClass();
        }
        
        @Override
        public T getRootBean() {
            return rootBean;
        }
    }
}