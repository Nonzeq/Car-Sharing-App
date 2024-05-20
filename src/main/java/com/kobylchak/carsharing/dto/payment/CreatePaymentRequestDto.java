package com.kobylchak.carsharing.dto.payment;

import com.kobylchak.carsharing.model.enums.PaymentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreatePaymentRequestDto {
    @NotNull
    @Positive
    private Long rentalId;
    @NotNull
    private PaymentType paymentType;
}
