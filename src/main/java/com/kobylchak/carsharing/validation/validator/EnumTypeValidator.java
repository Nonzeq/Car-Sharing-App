package com.kobylchak.carsharing.validation.validator;

import com.kobylchak.carsharing.validation.annotation.EnumType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumTypeValidator implements ConstraintValidator<EnumType, String> {
    private Class<? extends Enum<?>> enumClass;
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        value = value.toUpperCase();
        for (Enum<?> enumConstant : enumClass.getEnumConstants()) {
            if (enumConstant.name().equals(value)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void initialize(EnumType constraintAnnotation) {
        this.enumClass = constraintAnnotation.type();
    }
}
