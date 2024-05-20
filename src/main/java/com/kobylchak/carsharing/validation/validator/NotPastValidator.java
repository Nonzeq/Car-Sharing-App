package com.kobylchak.carsharing.validation.validator;

import com.kobylchak.carsharing.validation.annotation.NotPast;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class NotPastValidator implements ConstraintValidator<NotPast, LocalDate> {
    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value.isAfter(LocalDate.now());
    }
}
