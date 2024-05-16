package com.kobylchak.carsharing.service.payment.impl;

import com.kobylchak.carsharing.model.Rental;
import com.kobylchak.carsharing.model.enums.PaymentType;
import com.kobylchak.carsharing.service.payment.AmountToPayCalculator;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Component;

@Component
public class PaymentCalculator implements AmountToPayCalculator {
    @Override
    public BigDecimal calculate(Rental rental) {
        BigDecimal fee = rental.getCar().getDailyFee();
        long daysOfUse = ChronoUnit.DAYS.between(rental.getRentalDate(),
                                                 rental.getActualReturnDate());
        return fee.multiply(BigDecimal.valueOf(daysOfUse));
    }
    
    @Override
    public PaymentType getType() {
        return PaymentType.PAYMENT;
    }
}
