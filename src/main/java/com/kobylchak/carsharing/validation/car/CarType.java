package com.kobylchak.carsharing.validation.car;

import com.kobylchak.carsharing.validation.user.FieldMatchValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CarTypeValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CarType {
    String message() default "Invalid car type";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
