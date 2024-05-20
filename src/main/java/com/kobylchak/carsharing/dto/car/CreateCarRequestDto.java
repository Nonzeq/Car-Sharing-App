package com.kobylchak.carsharing.dto.car;

import com.kobylchak.carsharing.model.enums.CarType;
import com.kobylchak.carsharing.validation.annotation.EnumType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CreateCarRequestDto {
    @NotBlank
    private String model;
    @NotBlank
    private String brand;
    @NotNull
    @EnumType(type = CarType.class)
    private CarType type;
    @PositiveOrZero
    private int inventory;
    @Positive
    private BigDecimal dailyFee;
}
