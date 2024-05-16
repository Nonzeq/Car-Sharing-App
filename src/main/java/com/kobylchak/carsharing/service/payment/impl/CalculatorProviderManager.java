package com.kobylchak.carsharing.service.payment.impl;

import com.kobylchak.carsharing.model.enums.PaymentType;
import com.kobylchak.carsharing.service.payment.AmountToPayCalculator;
import com.kobylchak.carsharing.service.payment.CalculatorProvider;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CalculatorProviderManager implements CalculatorProvider {
    
    private final List<AmountToPayCalculator> amountToPayCalculators;
    
    @Override
    public AmountToPayCalculator getCalculator(PaymentType type) {
        return amountToPayCalculators.stream()
                       .filter(calculator ->
                                       calculator.getType().equals(type))
                       .findFirst().orElseThrow(
                               () -> new IllegalArgumentException(
                                       "Cannot provide calculator with type: " + type));
    }
}
