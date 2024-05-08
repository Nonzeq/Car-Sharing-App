package com.kobylchak.carsharing.service.payment;

import com.kobylchak.carsharing.dto.payment.CreatePaymentRequestDto;
import com.kobylchak.carsharing.dto.payment.PaymentDto;
import com.kobylchak.carsharing.model.User;
import java.util.List;

public interface PaymentService {
    PaymentDto createPayment(CreatePaymentRequestDto requestDto, User user);
    
    List<PaymentDto> getPayments(Long userId, User user);
}
