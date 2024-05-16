package com.kobylchak.carsharing.service.payment.impl;

import com.kobylchak.carsharing.model.Rental;
import com.kobylchak.carsharing.model.enums.PaymentType;
import com.kobylchak.carsharing.service.payment.AmountToPayCalculator;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Component;

@Component
public class FineCalculator implements AmountToPayCalculator {
    private static final BigDecimal FINE_MULTIPLIER = BigDecimal.valueOf(1.4);
    
    @Override
    public BigDecimal calculate(Rental rental) {
        BigDecimal fee = rental.getCar().getDailyFee();
        long daysOfOverdue = ChronoUnit.DAYS.between(rental.getReturnDate(),
                                                     rental.getActualReturnDate());
        return fee.multiply(BigDecimal.valueOf(daysOfOverdue)).multiply(FINE_MULTIPLIER);
    }
    
    @Override
    public PaymentType getType() {
        return PaymentType.FINE;
    }
}
