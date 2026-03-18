package io.modular.framework.driver;

import io.modular.framework.api.*;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator as SpringValidator;

import java.util.HashSet;
import java.util.Set;

/**
 * Spring验证器适配器
 */
public class SpringValidator implements Validator {
    
    private final SpringValidator springValidator;
    
    public SpringValidator() {
        // 默认使用Spring的DataBinder验证
        this.springValidator = null;
    }
    
    public SpringValidator(SpringValidator springValidator) {
        this.springValidator = springValidator;
    }
    
    @Override
    public boolean supports(Class<?> clazz) {
        if (springValidator != null) {
            return springValidator.supports(clazz);
        }
        // 默认支持所有类
        return true;
    }
    
    @Override
    public void validate(Object target, Errors errors) {
        if (springValidator != null) {
            springValidator.validate(target, errors);
        }
    }
    
    @Override
    public Set<ConstraintViolation<Object>> validate(Object target) {
        Set<ConstraintViolation<Object>> violations = new HashSet<>();
        
        Errors errors = new BeanPropertyBindingResult(target, target.getClass().getSimpleName());
        
        if (springValidator != null) {
            springValidator.validate(target, errors);
        }
        
        // 转换Spring错误为ConstraintViolation
        for (org.springframework.validation.FieldError fieldError : errors.getFieldErrors()) {
            violations.add(new SpringConstraintViolation<>(target, fieldError));
        }
        
        for (org.springframework.validation.ObjectError objectError : errors.getGlobalErrors()) {
            violations.add(new SpringConstraintViolation<>(target, objectError));
        }
        
        return violations;
    }
    
    private static class SpringConstraintViolation<T> implements ConstraintViolation<T> {
        private final T rootBean;
        private final String message;
        private final String propertyPath;
        private final Object invalidValue;
        
        SpringConstraintViolation(T rootBean, org.springframework.validation.FieldError error) {
            this.rootBean = rootBean;
            this.message = error.getDefaultMessage();
            this.propertyPath = error.getField();
            this.invalidValue = error.getRejectedValue();
        }
        
        SpringConstraintViolation(T rootBean, org.springframework.validation.ObjectError error) {
            this.rootBean = rootBean;
            this.message = error.getDefaultMessage();
            this.propertyPath = error.getObjectName();
            this.invalidValue = null;
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