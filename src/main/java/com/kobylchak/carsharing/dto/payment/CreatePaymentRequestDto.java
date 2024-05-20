package com.kobylchak.carsharing.dto.payment;

import com.kobylchak.carsharing.model.enums.PaymentType;
import com.kobylchak.carsharing.validation.annotation.EnumType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreatePaymentRequestDto {
    @NotNull
    @Positive
    private Long rentalId;
    @NotNull
    @EnumType(type = PaymentType.class)
    private PaymentType paymentType;
}
