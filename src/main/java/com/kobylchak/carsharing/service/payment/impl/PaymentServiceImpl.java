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
import com.kobylchak.carsharing.service.notification.NotificationService;
import com.kobylchak.carsharing.service.payment.PaymentService;
import com.kobylchak.carsharing.service.stripe.StripeInternalService;
import com.stripe.model.checkout.Session;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final StripeInternalService stripeService;
    private final RentalRepository rentalRepository;
    private final NotificationService notificationService;
    
    @Override
    @SneakyThrows
    public PaymentDto createPayment(CreatePaymentRequestDto requestDto, User user) {
        Rental rental = rentalRepository.findById(requestDto.getRentalId()).orElseThrow(
                () -> new EntityNotFoundException("Rental with id: "
                                                  + requestDto.getRentalId()
                                                  + " not found"));
        Session session = stripeService.createSession(user, rental);
        Payment payment = new Payment();
        payment.setAmountToPay(BigDecimal.valueOf(session.getAmountTotal())
                                       .divide(BigDecimal.valueOf(100), RoundingMode.HALF_DOWN));
        payment.setRental(rental);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setType(PaymentType.PAYMENT);
        payment.setSessionId(session.getId());
        payment.setSessionUrl(new URL(session.getUrl()));
 
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
    
    @Override
    public String success(String sessionId) {
        Payment payment = paymentRepository.findBySessionId(sessionId).orElseThrow(
                () -> new EntityNotFoundException("Can't find Payment by session id: "
                                                  + sessionId));
        if (stripeService.checkSuccess(sessionId)) {
            payment.setStatus(PaymentStatus.PAID);
            paymentRepository.save(payment);
            notificationService.sendNotification("Paiment for sessionId: "
                                                + sessionId
                                                + " was successful");
            return "Your payment has been completed successfully";
        }
        return "Something went wrong";
    }
    
    @Override
    public void cancel(String sessionId) {
    
    }
}
