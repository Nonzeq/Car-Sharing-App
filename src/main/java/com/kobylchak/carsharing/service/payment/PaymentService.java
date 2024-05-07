package com.kobylchak.carsharing.service.payment;

import com.kobylchak.carsharing.dto.payment.CreatePaymentRequestDto;
import com.kobylchak.carsharing.dto.payment.PaymentDto;
import com.kobylchak.carsharing.dto.payment.SearchPaymentParameters;
import com.kobylchak.carsharing.model.User;

public interface PaymentService {
    PaymentDto createPayment(CreatePaymentRequestDto requestDto);
    
    PaymentDto getPayment(SearchPaymentParameters parameters);
}
