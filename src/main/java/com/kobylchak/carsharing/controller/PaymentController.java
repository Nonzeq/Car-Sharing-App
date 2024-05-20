package com.kobylchak.carsharing.controller;

import com.kobylchak.carsharing.dto.payment.CreatePaymentRequestDto;
import com.kobylchak.carsharing.dto.payment.PaymentCancelDto;
import com.kobylchak.carsharing.dto.payment.PaymentDto;
import com.kobylchak.carsharing.dto.payment.PaymentSuccessDto;
import com.kobylchak.carsharing.model.User;
import com.kobylchak.carsharing.service.payment.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/payments")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(
        name = "Payments management",
        description = "Endpoints for payments management"
)
public class PaymentController {
    private final PaymentService paymentService;
    
    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('MANAGER', 'CUSTOMER')")
    @Operation(summary = "Get list of all available payments by user id or for current user")
    public List<PaymentDto> getPayments(
                @RequestParam("user_id") Long userId,
                Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return paymentService.getPayments(userId, user);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Create a new payment")
    public PaymentDto createPayment(@RequestBody CreatePaymentRequestDto requestDto,
                                                  Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return paymentService.createPayment(requestDto, user);
    }
    
    @GetMapping("/success")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Endpoint for Stripe checkout session success URL")
    public PaymentSuccessDto success(@RequestParam("session_id") String sessionId) {
        return paymentService.success(sessionId);
    }
    
    @GetMapping("/cancel")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Endpoint for Stripe checkout session cancel URL")
    public PaymentCancelDto cancel(@RequestParam("session_id") String sessionId) {
        return paymentService.cancel(sessionId);
    }
}
