package com.kobylchak.carsharing.service.payment;

import com.kobylchak.carsharing.dto.payment.CreatePaymentRequestDto;
import com.kobylchak.carsharing.dto.payment.PaymentDto;
import com.kobylchak.carsharing.dto.payment.SuccessDto;
import com.kobylchak.carsharing.exception.PaymentException;
import com.kobylchak.carsharing.model.User;
import java.util.List;

public interface PaymentService {
    PaymentDto createPayment(CreatePaymentRequestDto requestDto, User user) throws PaymentException;
    
    List<PaymentDto> getPayments(Long userId, User user);
    
    SuccessDto success(String sessionId);
    
    void cancel(String sessionId);
}
