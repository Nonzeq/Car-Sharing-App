package com.kobylchak.carsharing.service.payment.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.kobylchak.carsharing.model.Car;
import com.kobylchak.carsharing.model.Rental;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FineCalculatorTest {
    private static final BigDecimal FINE_MULTIPLIER = BigDecimal.valueOf(1.4);
    @InjectMocks
    private FineCalculator fineCalculator;
    
    @Test
    public void calculate_ValidParameters_ShouldReturnBigDecimal() {
        final BigDecimal dayleFee = BigDecimal.valueOf(40);
        final int daysOfRent = 10;
        final int daysOfOverdue = 5;
        Car car = new Car();
        car.setDailyFee(dayleFee);
        Rental rental = new Rental();
        LocalDate rentalDate = LocalDate.now().minusDays(daysOfRent);
        rental.setRentalDate(rentalDate);
        rental.setReturnDate(LocalDate.now());
        rental.setActualReturnDate(LocalDate.now().plusDays(daysOfOverdue));
        rental.setCar(car);
        BigDecimal expected = BigDecimal.valueOf(daysOfOverdue)
                                      .multiply(dayleFee)
                                      .multiply(FINE_MULTIPLIER);
        
        BigDecimal actual = fineCalculator.calculate(rental);
        assertEquals(expected, actual);
    }
}
