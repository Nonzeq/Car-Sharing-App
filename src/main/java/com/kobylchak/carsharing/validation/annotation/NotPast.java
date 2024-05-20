package com.kobylchak.carsharing.validation.annotation;

import com.kobylchak.carsharing.validation.validator.NotPastValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NotPastValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotPast {
    String message() default "The date has passed";
    
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
