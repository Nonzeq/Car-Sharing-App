package com.kobylchak.carsharing.dto.rental;

import com.kobylchak.carsharing.validation.annotation.NotPast;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import lombok.Data;

@Data
public class CreateRentalRequestDto {
    @NotNull
    @Positive
    private Long carId;
    @NotNull
    @NotPast
    private LocalDate returnDate;
}
