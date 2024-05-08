package com.kobylchak.carsharing.dto.payment;

import lombok.Data;

@Data
public class CreatePaymentRequestDto {
    private Long rentalId;
    private String type;
}
