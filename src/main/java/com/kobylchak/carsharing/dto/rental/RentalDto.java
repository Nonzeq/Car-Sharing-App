package com.kobylchak.carsharing.dto.rental;

import com.kobylchak.carsharing.dto.car.CarDto;
import java.time.LocalDate;
import lombok.Data;

@Data
public class RentalDto {
    private Long id;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private LocalDate actualReturnDate;
    private CarDto car;
}
