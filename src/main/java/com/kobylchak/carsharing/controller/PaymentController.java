package com.kobylchak.carsharing.controller;

import com.kobylchak.carsharing.dto.payment.CreatePaymentRequestDto;
import com.kobylchak.carsharing.dto.payment.PaymentDto;
import com.kobylchak.carsharing.model.User;
import com.kobylchak.carsharing.service.payment.PaymentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    
    @GetMapping("/")
    public @ResponseBody List<PaymentDto> getPayments(
                @RequestParam("user_id") Long userId,
                Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return paymentService.getPayments(userId, user);
    }
    
    @PostMapping("/")
    public @ResponseBody PaymentDto createPayment(@RequestBody CreatePaymentRequestDto requestDto,
                                                  Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return paymentService.createPayment(requestDto, user);
    }
    
    @GetMapping("/success")
    public String success() {
        return "success_payment";
    }
}
