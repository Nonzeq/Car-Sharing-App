package com.kobylchak.carsharing.service.stripe.impl;

import static com.stripe.param.checkout.SessionCreateParams.Builder;
import static com.stripe.param.checkout.SessionCreateParams.LineItem;
import static com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData;
import static com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData.ProductData;

import com.kobylchak.carsharing.model.Payment;
import com.kobylchak.carsharing.model.User;
import com.kobylchak.carsharing.service.stripe.StripeInternalService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeInternalServiceImpl implements StripeInternalService {
    private static final String CURRENCY = "USD";
    private static final String CHECKOUT_SESSION_ID = "?session_id={CHECKOUT_SESSION_ID}";
    private static final long QUANTITY = 1;
    private static final long STRIPE_AMOUNT_MULTIPLIER = 100;
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
    public Session createSession(User user, Payment payment) {
        try {
            Builder builder = SessionCreateParams.builder()
                                      .setCustomerEmail(user.getEmail())
                                      .setSuccessUrl(successUrl + CHECKOUT_SESSION_ID)
                                      .setCancelUrl(cancelUrl)
                                      .setMode(SessionCreateParams.Mode.PAYMENT)
                                      .addPaymentMethodType(SessionCreateParams
                                                                    .PaymentMethodType.CARD)
                                      .addLineItem(getLineItem(payment));
            return Session.create(builder.build());
        } catch (StripeException e) {
            throw new RuntimeException("Creating session error", e);
        }
    }
    
    @Override
    public boolean checkSuccess(String sessionId) {
        try {
            Session session = Session.retrieve(sessionId);
            return session.getPaymentStatus().equals("paid");
        } catch (StripeException e) {
            throw new RuntimeException("Can't retrieve session with id: " + sessionId, e);
        }
    }
    
    private LineItem getLineItem(Payment payment) {
        return LineItem.builder()
                       .setQuantity(QUANTITY)
                       .setPriceData(
                               PriceData.builder()
                                       .setCurrency(CURRENCY)
                                       .setUnitAmountDecimal(
                                               getUnitAmount(payment.getAmountToPay()))
                                       .setProductData(
                                               ProductData.builder()
                                                       .setName(payment
                                                                        .getRental()
                                                                        .getCar()
                                                                        .getModel())
                                                       .setDescription("Payment for car rental")
                                                       .build()
                                                      )
                                       .build()
                                    )
                       .build();
    }
    
    private BigDecimal getUnitAmount(BigDecimal amountToPay) {
        return amountToPay.multiply(BigDecimal.valueOf(STRIPE_AMOUNT_MULTIPLIER));
    }
}
