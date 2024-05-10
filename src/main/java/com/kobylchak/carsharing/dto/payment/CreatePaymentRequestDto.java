package com.kobylchak.carsharing.dto.payment;

import com.kobylchak.carsharing.model.enums.PaymentType;
import lombok.Data;

@Data
public class CreatePaymentRequestDto {
    private Long rentalId;
    private PaymentType paymentType;
}
