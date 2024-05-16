package com.kobylchak.carsharing.service.payment;

import com.kobylchak.carsharing.model.Rental;
import com.kobylchak.carsharing.model.enums.PaymentType;
import java.math.BigDecimal;


public interface AmountToPayCalculator {
    BigDecimal calculate(Rental rental);
    
    PaymentType getType();
}
