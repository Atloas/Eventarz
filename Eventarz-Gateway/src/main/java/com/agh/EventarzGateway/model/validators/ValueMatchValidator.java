package com.agh.EventarzGateway.model.validators;

import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValueMatchValidator implements ConstraintValidator<ValueMatch, Object> {

    private String field;
    private String otherField;

    public void initialize(ValueMatch constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.otherField = constraintAnnotation.otherField();
    }

    public boolean isValid(Object value, ConstraintValidatorContext context) {

        Object fieldValue = new BeanWrapperImpl(value).getPropertyValue(field);
        Object otherFieldValue = new BeanWrapperImpl(value).getPropertyValue(otherField);

        if (fieldValue != null) {
            return fieldValue.equals(otherFieldValue);
        } else {
            return otherFieldValue == null;
        }
    }
}
