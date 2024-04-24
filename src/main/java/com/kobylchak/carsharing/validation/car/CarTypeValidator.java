package com.kobylchak.carsharing.validation.car;

import com.kobylchak.carsharing.model.Car;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CarTypeValidator implements ConstraintValidator<CarType, String> {
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        value = value.toUpperCase();
        for (Car.CarType carType : Car.CarType.values()) {
            if (carType.name().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
