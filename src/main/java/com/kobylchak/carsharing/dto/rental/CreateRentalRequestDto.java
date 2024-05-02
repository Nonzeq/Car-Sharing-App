package com.kobylchak.carsharing.dto.rental;

import java.time.LocalDate;
import lombok.Data;

@Data
public class CreateRentalRequestDto {
    private Long carId;
    private LocalDate returnDate;
}
