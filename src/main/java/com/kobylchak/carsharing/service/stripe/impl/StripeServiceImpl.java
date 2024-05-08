package com.kobylchak.carsharing.service.stripe.impl;

import com.kobylchak.carsharing.model.User;
import com.kobylchak.carsharing.service.stripe.StripeService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeServiceImpl implements StripeService {
    
    @Value("${url.success}")
    private String successUrl;
    @Value("${url.cancel}")
    private String cancelUrl;
    
    @Value("${stripe.api.secret.key}")
    private String stripePrivateKey;
    
    @PostConstruct
    public void init() {
        Stripe.apiKey = stripePrivateKey;
    }
    
    @Override
    public Session createSession(User user) {
        
        SessionCreateParams.Builder builder = SessionCreateParams.builder()
                                                      .setCustomerEmail(user.getEmail())
                                                      .setSuccessUrl(successUrl)
                                                      .setCancelUrl(cancelUrl)
                                                      .setMode(SessionCreateParams.Mode.PAYMENT);
                                                      
        
        builder
                .addLineItem(SessionCreateParams.LineItem.builder().setQuantity(1L)
                                     .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                                           .setProductData(
                                                                   SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                           .setName("test")
                                                                           .build())
                                                           .setCurrency("USD").setUnitAmount(3333L)
                                                           .build())
                                     .build());
        
        try {
            return Session.create(builder.build());
        } catch (StripeException e) {
            throw new RuntimeException("Creating session error", e);
        }
    }
}
