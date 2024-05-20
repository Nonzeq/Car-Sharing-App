package com.kobylchak.carsharing.validation.annotation;

import com.kobylchak.carsharing.validation.validator.EnumTypeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EnumTypeValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumType {
    String message() default "Invalid enum type";
    Class<? extends Enum<?>> type();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
