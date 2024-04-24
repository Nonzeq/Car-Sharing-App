package com.kobylchak.carsharing.dto.car;

import com.kobylchak.carsharing.validation.car.CarType;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank
    @CarType
    private String type;
    @PositiveOrZero
    private int inventory;
    @Positive
    private BigDecimal dailyFee;
}
