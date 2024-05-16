package com.kobylchak.carsharing.service.payment;

import com.kobylchak.carsharing.model.enums.PaymentType;

public interface CalculatorProvider {
    AmountToPayCalculator getCalculator(PaymentType type);
}
