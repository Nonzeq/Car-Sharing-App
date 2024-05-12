package com.kobylchak.carsharing.service.payment.impl;

import com.kobylchak.carsharing.dto.payment.CreatePaymentRequestDto;
import com.kobylchak.carsharing.dto.payment.PaymentDto;
import com.kobylchak.carsharing.dto.payment.SuccessDto;
import com.kobylchak.carsharing.exception.PaymentException;
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
import java.net.URL;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private static final BigDecimal FINE_MULTIPLIER = BigDecimal.valueOf(1.4);
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final StripeInternalService stripeService;
    private final RentalRepository rentalRepository;
    private final NotificationService notificationService;
    
    @Override
    @SneakyThrows
    public PaymentDto createPayment(CreatePaymentRequestDto requestDto, User user)
            throws PaymentException {
        if (paymentRepository.findByRentalIdAndType(requestDto.getRentalId(),
                                                    requestDto.getPaymentType()).isPresent()) {
            throw new PaymentException("Payment for rental id: " + requestDto.getRentalId()
                                       + " already created");
        }
        Rental rental = rentalRepository.findById(requestDto.getRentalId()).orElseThrow(
                () -> new EntityNotFoundException("Rental with id: "
                                                  + requestDto.getRentalId()
                                                  + " not found"));
        Payment payment = createPaymentEntity(rental, requestDto.getPaymentType());
        Session session = stripeService.createSession(user, payment);
        payment.setSessionUrl(new URL(session.getUrl()));
        payment.setSessionId(session.getId());
        return paymentMapper.toDto(paymentRepository.save(payment));
    }
    
    @Override
    public List<PaymentDto> getPayments(Long userId, User user) {
        if (user.getRole().getName().equals(UserRole.CUSTOMER)) {
            userId = user.getId();
        }
        List<Rental> rentals = rentalRepository.findAllByUserId(userId);
        List<Long> list = rentals.stream()
                                  .map(Rental::getId)
                                  .toList();
        return paymentMapper.toDtos(paymentRepository.findAllById(list));
    }
    
    @Override
    public SuccessDto success(String sessionId) {
        Payment payment = paymentRepository.findBySessionId(sessionId).orElseThrow(
                () -> new EntityNotFoundException("Can't find Payment by session id: "
                                                  + sessionId));
        if (stripeService.checkSuccess(sessionId)
                && !payment.getStatus().equals(PaymentStatus.PAID)) {
            payment.setStatus(PaymentStatus.PAID);
            paymentRepository.save(payment);
            notificationService.sendNotification("Payment for sessionId: "
                                                 + sessionId
                                                 + " was successful");
            return new SuccessDto("Your payment has been completed successfully");
        }
        return new SuccessDto("Something went wrong");
    }
    
    @Override
    public void cancel(String sessionId) {
    
    }
    
    private Payment createPaymentEntity(Rental rental, PaymentType paymentType) {
        Payment payment = new Payment();
        payment.setType(paymentType);
        payment.setRental(rental);
        payment.setAmountToPay(calculateAmountToPay(rental, paymentType));
        payment.setStatus(PaymentStatus.PENDING);
        return payment;
    }
    
    private BigDecimal calculateAmountToPay(Rental rental, PaymentType paymentType) {
        BigDecimal fee = rental.getCar().getDailyFee();
        if (paymentType.equals(PaymentType.PAYMENT)) {
            long daysOfUse = ChronoUnit.DAYS.between(rental.getRentalDate(),
                                                     rental.getReturnDate());
            return fee.multiply(BigDecimal.valueOf(daysOfUse));
        }
        long daysOfOverdue = ChronoUnit.DAYS.between(rental.getReturnDate(),
                                                     rental.getActualReturnDate());
        return fee.multiply(BigDecimal.valueOf(daysOfOverdue)).multiply(FINE_MULTIPLIER);
    }
}
