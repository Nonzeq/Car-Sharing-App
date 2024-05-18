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
class PaymentCalculatorTest {
    @InjectMocks
    private PaymentCalculator paymentCalculator;
    
    @Test
    public void calculate_ValidParameters_ShouldReturnBigDecimal() {
        final BigDecimal dayleFee = BigDecimal.valueOf(40);
        final int daysOfRent = 10;
        Car car = new Car();
        car.setDailyFee(dayleFee);
        Rental rental = new Rental();
        LocalDate rentalDate = LocalDate.now().minusDays(daysOfRent);
        rental.setRentalDate(rentalDate);
        rental.setActualReturnDate(LocalDate.now());
        rental.setCar(car);
        BigDecimal expected = dayleFee.multiply(BigDecimal.valueOf(daysOfRent));
        
        BigDecimal actual = paymentCalculator.calculate(rental);
        assertEquals(expected, actual);
    }
}
