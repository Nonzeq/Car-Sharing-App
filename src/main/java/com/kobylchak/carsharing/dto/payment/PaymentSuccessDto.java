package com.kobylchak.carsharing.dto.payment;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentSuccessDto {
    private String message;
    
    public PaymentSuccessDto(String message) {
        this.message = message;
    }
}
