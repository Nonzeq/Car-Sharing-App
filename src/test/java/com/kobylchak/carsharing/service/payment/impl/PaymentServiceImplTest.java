package com.kobylchak.carsharing.service.payment.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.kobylchak.carsharing.dto.payment.CreatePaymentRequestDto;
import com.kobylchak.carsharing.dto.payment.PaymentCancelDto;
import com.kobylchak.carsharing.dto.payment.PaymentDto;
import com.kobylchak.carsharing.dto.payment.PaymentSuccessDto;
import com.kobylchak.carsharing.exception.PaymentException;
import com.kobylchak.carsharing.mapper.PaymentMapper;
import com.kobylchak.carsharing.model.Car;
import com.kobylchak.carsharing.model.Payment;
import com.kobylchak.carsharing.model.Rental;
import com.kobylchak.carsharing.model.Role;
import com.kobylchak.carsharing.model.User;
import com.kobylchak.carsharing.model.enums.PaymentStatus;
import com.kobylchak.carsharing.model.enums.PaymentType;
import com.kobylchak.carsharing.model.enums.UserRole;
import com.kobylchak.carsharing.repository.payment.PaymentRepository;
import com.kobylchak.carsharing.repository.rental.RentalRepository;
import com.kobylchak.carsharing.service.notification.impl.TelegramNotificationService;
import com.kobylchak.carsharing.service.payment.CalculatorProvider;
import com.kobylchak.carsharing.service.stripe.impl.StripeInternalServiceImpl;
import com.stripe.model.checkout.Session;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {
    @InjectMocks
    private PaymentServiceImpl paymentService;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private StripeInternalServiceImpl stripeService;
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private CalculatorProvider calculatorProvider;
    @Mock
    private PaymentCalculator paymentCalculator;
    @Mock
    private TelegramNotificationService notificationService;
    
    @Test
    public void createPayment_ValidParameters_ShouldReturnPaymentDto() {
        final Long rentalId = 1L;
        final String sessionId = "sessionId";
        final String sessionUrl = "https://session.url.com/";
        
        User user = new User();
        user.setId(1L);
        user.setFirstName("first");
        user.setLastName("last");
        user.setEmail("user@example.com");
        
        CreatePaymentRequestDto requestDto = new CreatePaymentRequestDto();
        requestDto.setPaymentType(PaymentType.PAYMENT);
        requestDto.setRentalId(rentalId);
        
        Car car = new Car();
        car.setDailyFee(BigDecimal.valueOf(40));
        
        Rental rental = new Rental();
        rental.setId(rentalId);
        rental.setCar(car);
        rental.setRentalDate(LocalDate.now().minusDays(10));
        rental.setReturnDate(LocalDate.now());
        rental.setActualReturnDate(LocalDate.now());
        
        Payment payment = new Payment();
        payment.setId(22L);
        payment.setRental(rental);
        
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setRentalId(payment.getId());
        
        Session session = new Session();
        session.setId(sessionId);
        session.setUrl(sessionUrl);
        
        when(paymentRepository.findByRentalIdAndType(requestDto.getRentalId(),
                                                     requestDto.getPaymentType())).thenReturn(
                Optional.empty());
        when(rentalRepository.findByIdAndActualReturnDateIsExist(
                requestDto.getRentalId())).thenReturn(Optional.of(rental));
        when(calculatorProvider.getCalculator(requestDto.getPaymentType())).thenReturn(
                paymentCalculator);
        when(paymentCalculator.calculate(rental)).thenReturn(BigDecimal.valueOf(90));
        when(paymentMapper.fromRentalToPayment(
                rental,
                requestDto.getPaymentType(),
                PaymentStatus.PENDING,
                BigDecimal.valueOf(90)))
                .thenReturn(payment);
        
        when(stripeService.createSession(user, payment)).thenReturn(session);
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);
        
        PaymentDto createdPayment = paymentService.createPayment(requestDto, user);
        
        assertNotNull(createdPayment);
        assertEquals(paymentDto.getRentalId(), createdPayment.getRentalId());
    }
    
    @Test
    public void createPayment_WithAlreadyCreatedPaymentIdAndType_ShouldThrowPaymentException() {
        Long rentalId = 1L;
        CreatePaymentRequestDto requestDto = new CreatePaymentRequestDto();
        requestDto.setPaymentType(PaymentType.PAYMENT);
        requestDto.setRentalId(rentalId);
        
        User user = new User();
        user.setId(1L);
        user.setFirstName("first");
        user.setLastName("last");
        user.setEmail("user@example.com");
        
        when(paymentRepository.findByRentalIdAndType(requestDto.getRentalId(),
                                                     requestDto.getPaymentType())).thenReturn(
                Optional.of(new Payment()));
        
        PaymentException paymentException = assertThrows(PaymentException.class,
                                                         () -> paymentService.createPayment(
                                                                 requestDto, user));
        String expected = "Payment for rental id: " + requestDto.getRentalId() + " already created";
        assertEquals(expected, paymentException.getMessage());
    }
    
    @Test
    public void successMethod_InvalidSessionId_ShouldThrowEntityNotFoundException() {
        String sessionId = "asdasdasd";
        
        when(paymentRepository.findBySessionId(sessionId)).thenReturn(Optional.empty());
        
        EntityNotFoundException paymentException = assertThrows(EntityNotFoundException.class,
                                                                () -> paymentService.success(
                                                                        sessionId));
        String expected = "Can't find Payment by session id: " + sessionId;
        assertEquals(expected, paymentException.getMessage());
    }
    
    @Test
    public void successMethod_ValidSessionId_ShouldReturnPaymentSuccessDto() {
        String sessionId = "validSessionId";
        
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PENDING);
        
        when(paymentRepository.findBySessionId(sessionId)).thenReturn(Optional.of(payment));
        when(stripeService.checkSuccess(sessionId)).thenReturn(true);
        when(paymentRepository.save(payment)).thenReturn(payment);
        
        when(notificationService.messageBuilder())
                .thenCallRealMethod();
        PaymentSuccessDto successDto = paymentService.success(sessionId);
        String expected = "Your payment has been completed successfully";
        
        assertNotNull(successDto);
        assertEquals(expected, successDto.getMessage());
    }
    
    @Test
    public void successMethod_PaymentWasAlreadyPaid_ShouldReturnPaymentSuccessDto() {
        String sessionId = "asdasdasd";
        
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PAID);
        
        when(paymentRepository.findBySessionId(sessionId)).thenReturn(Optional.of(payment));
        when(stripeService.checkSuccess(sessionId)).thenReturn(true);
        
        PaymentSuccessDto successDto = paymentService.success(sessionId);
        String expected = "Something went wrong";
        
        assertEquals(expected, successDto.getMessage());
    }
    
    @Test
    public void cancelMethod_InvalidSessionId_ShouldThrowEntityNotFoundException() {
        String sessionId = "asdasdasd";
        
        when(paymentRepository.findBySessionId(sessionId)).thenReturn(Optional.empty());
        
        EntityNotFoundException entityNotFoundException = assertThrows(
                EntityNotFoundException.class, () -> paymentService.cancel(sessionId));
        String expected = "Can't find Payment by session id: " + sessionId;
        
        assertEquals(expected, entityNotFoundException.getMessage());
    }
    
    @Test
    public void cancelMethod_SessionHasExpired_ShouldReturnPaymentCancelDto() {
        String sessionId = "asdasdasd";
        String sessionUrl = "sessionUrl";
        Session session = new Session();
        session.setUrl(sessionUrl);
        
        when(paymentRepository.findBySessionId(sessionId)).thenReturn(Optional.of(new Payment()));
        when(stripeService.getSessionById(sessionId)).thenReturn(session);
        when(stripeService.checkCancel(sessionId)).thenReturn(false);
        
        PaymentCancelDto cancel = paymentService.cancel(sessionId);
        String expectedMessage = "Your session has expired";
        
        assertEquals(expectedMessage, cancel.getMessage());
        assertEquals(sessionUrl, cancel.getSessionUrl());
    }
    
    @Test
    public void cancelMethod_ValidSessionNotExpired_ShouldReturnPaymentCancelDto() {
        String sessionId = "asdasdasd";
        String sessionUrl = "sessionUrl";
        Session session = new Session();
        session.setUrl(sessionUrl);
        
        when(paymentRepository.findBySessionId(sessionId)).thenReturn(Optional.of(new Payment()));
        when(stripeService.getSessionById(sessionId)).thenReturn(session);
        when(stripeService.checkCancel(sessionId)).thenReturn(true);
        
        PaymentCancelDto cancel = paymentService.cancel(sessionId);
        String expectedMessage = "Payment can be made later " + "the session is available " + " "
                                 + "24 hours";
        
        assertEquals(expectedMessage, cancel.getMessage());
        assertEquals(sessionUrl, cancel.getSessionUrl());
    }
    
    @Test
    public void createPayment_InvalidIdOrRentalNotReturned_ShouldThrowEntityNotFoundException() {
        Long rentalId = 1L;
        CreatePaymentRequestDto requestDto = new CreatePaymentRequestDto();
        requestDto.setPaymentType(PaymentType.PAYMENT);
        requestDto.setRentalId(rentalId);
        
        User user = new User();
        user.setId(1L);
        user.setFirstName("first");
        user.setLastName("last");
        user.setEmail("user@example.com");
        
        when(rentalRepository.findByIdAndActualReturnDateIsExist(
                requestDto.getRentalId())).thenReturn(Optional.empty());
        
        EntityNotFoundException paymentException = assertThrows(EntityNotFoundException.class,
                                                                () -> paymentService.createPayment(
                                                                        requestDto, user));
        String expected = "Rental with id: " + requestDto.getRentalId() + " not found or not "
                          + "returned";
        assertEquals(expected, paymentException.getMessage());
    }
    
    @Test
    public void getPayments_InvalidIdOrRentalNotReturned_ShouldThrowEntityNotFoundException() {
        final Long rentalId = 1L;
        final Long userId = 1L;
        CreatePaymentRequestDto requestDto = new CreatePaymentRequestDto();
        requestDto.setPaymentType(PaymentType.PAYMENT);
        requestDto.setRentalId(rentalId);
        Role customerRole = new Role();
        customerRole.setName(UserRole.CUSTOMER);
        Role managerRole = new Role();
        managerRole.setName(UserRole.MANAGER);
        
        User user = new User();
        user.setId(userId);
        user.setFirstName("first");
        user.setLastName("last");
        user.setEmail("user@example.com");
        user.setRole(customerRole);
        
        Rental rental = new Rental();
        rental.setId(rentalId);
        
        List<Long> rentalIds = List.of(rentalId);
        List<Rental> rentals = List.of(rental);
        List<Payment> payments = List.of(new Payment());
        List<PaymentDto> paymentsDtos = List.of(new PaymentDto());
        
        when(rentalRepository.findAllByUserId(userId)).thenReturn(rentals);
        when(paymentRepository.findAllByRentalId(rentalIds)).thenReturn(payments);
        when(paymentMapper.toDtos(payments)).thenReturn(paymentsDtos);
        
        List<PaymentDto> actual = paymentService.getPayments(userId, user);
        
        assertNotNull(actual);
        assertEquals(paymentsDtos.size(), actual.size());
    }
}
