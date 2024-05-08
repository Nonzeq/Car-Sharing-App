package com.kobylchak.carsharing.service.payment.impl;

import com.kobylchak.carsharing.dto.payment.CreatePaymentRequestDto;
import com.kobylchak.carsharing.dto.payment.PaymentDto;
import com.kobylchak.carsharing.mapper.payment.PaymentMapper;
import com.kobylchak.carsharing.model.Payment;
import com.kobylchak.carsharing.model.Rental;
import com.kobylchak.carsharing.model.User;
import com.kobylchak.carsharing.model.enums.PaymentStatus;
import com.kobylchak.carsharing.model.enums.PaymentType;
import com.kobylchak.carsharing.model.enums.UserRole;
import com.kobylchak.carsharing.repository.payment.PaymentRepository;
import com.kobylchak.carsharing.repository.rental.RentalRepository;
import com.kobylchak.carsharing.service.payment.PaymentService;
import com.kobylchak.carsharing.service.stripe.StripeService;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final StripeService stripeService;
    private final RentalRepository rentalRepository;
    
    @Override
    public PaymentDto createPayment(CreatePaymentRequestDto requestDto, User user) {
        Session session = stripeService.createSession(user);
        Payment payment = new Payment();
        payment.setAmountToPay(BigDecimal.valueOf(100));
        payment.setRental(rentalRepository.findById(1L).orElseThrow());
        payment.setStatus(PaymentStatus.PAID);
        payment.setType(PaymentType.PAYMENT);
        payment.setSessionId(session.getId());
        try {
            payment.setSessionUrl(new URL(session.getUrl()));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return paymentMapper.toDto(paymentRepository.save(payment));
    }
    
    @Override
    public List<PaymentDto> getPayments(Long userId, User user) {
        if (user.getRole().getName().equals(UserRole.MANAGER)) {
            List<Rental> rentals = rentalRepository.findAllByUserId(userId);
            List<Long> list = rentals.stream()
                                     .map(Rental::getId)
                                     .toList();
            return paymentMapper.toDtos(paymentRepository.findAllById(list));
        }
        List<Rental> rentals = rentalRepository.findAllByUserId(userId);
        List<Long> list = rentals.stream()
                                 .map(Rental::getId)
                                 .toList();
        return paymentMapper.toDtos(paymentRepository.findAllById(list));
    }
}
