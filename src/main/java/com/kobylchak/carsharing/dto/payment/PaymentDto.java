package com.kobylchak.carsharing.dto.payment;

import com.kobylchak.carsharing.model.enums.PaymentStatus;
import com.kobylchak.carsharing.model.enums.PaymentType;
import java.math.BigDecimal;
import java.net.URL;
import lombok.Data;

@Data
public class PaymentDto {
    private Long id;
    private URL sessionUrl;
    private PaymentStatus status;
    private PaymentType type;
    private BigDecimal amountToPay;
    private Long rentalId;
}
