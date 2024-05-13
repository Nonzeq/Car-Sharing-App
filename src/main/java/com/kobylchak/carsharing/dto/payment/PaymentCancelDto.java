package com.kobylchak.carsharing.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCancelDto {
    private String message;
    private String sessionUrl;
}
