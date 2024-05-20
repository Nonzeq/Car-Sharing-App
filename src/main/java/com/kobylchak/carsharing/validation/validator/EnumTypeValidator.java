package com.kobylchak.carsharing.validation.validator;

import com.kobylchak.carsharing.validation.annotation.EnumType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumTypeValidator implements ConstraintValidator<EnumType, Enum<?>> {
    private Class<? extends Enum<?>> enumClass;
    
    @Override
    public void initialize(EnumType constraintAnnotation) {
        this.enumClass = constraintAnnotation.type();
    }
    
    @Override
    public boolean isValid(Enum value, ConstraintValidatorContext context) {
        for (Enum<?> enumConstant : enumClass.getEnumConstants()) {
            if (enumConstant.equals(value)) {
                return true;
            }
        }
        return false;
    }
}
