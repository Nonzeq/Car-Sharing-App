package com.kobylchak.carsharing.service.stripe.impl;

import static com.stripe.param.checkout.SessionCreateParams.LineItem;
import static com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData;
import static com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData.ProductData;
import static com.stripe.param.checkout.SessionCreateParams.Builder;

import com.kobylchak.carsharing.model.Rental;
import com.kobylchak.carsharing.model.User;
import com.kobylchak.carsharing.service.stripe.StripeInternalService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeInternalServiceImpl implements StripeInternalService {
    private static final String CURRENCY = "USD";
    private static final String CHECKOUT_SESSION_ID = "?session_id={CHECKOUT_SESSION_ID}";
    private static final BigDecimal FINE_MULTIPLIER = BigDecimal.valueOf(20);
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
    public Session createSession(User user, Rental rental) {
        try {
            Builder builder = SessionCreateParams.builder()
                                      .setCustomerEmail(user.getEmail())
                                      .setSuccessUrl(successUrl + CHECKOUT_SESSION_ID)
                                      .setCancelUrl(cancelUrl)
                                      .setMode(SessionCreateParams.Mode.PAYMENT)
                                      .addPaymentMethodType(SessionCreateParams
                                                                    .PaymentMethodType.CARD)
                                      .addLineItem(getLineItem(rental));
            return Session.create(builder.build());
        } catch (StripeException e) {
            throw new RuntimeException("Creating session error", e);
        }
    }
    
    @Override
    public boolean checkSuccess(String sessionId) {
        try {
            Session session = Session.retrieve(sessionId);
            return session.getStatus().equals("complete");
        } catch (StripeException e) {
            throw new RuntimeException("Can't retrieve session with id: " + sessionId, e);
        }
    }
    
    private LineItem getLineItem(Rental rental) {
        return LineItem.builder()
                       .setQuantity(1L)
                       .setPriceData(
                               PriceData.builder()
                                       .setCurrency(CURRENCY)
                                       .setUnitAmountDecimal(getAmount(rental))
                                       .setProductData(
                                               ProductData.builder()
                                                       .setName(rental.getCar().getModel())
                                                       .setDescription("Car rental")
                                                       .build()
                                                      )
                                       .build()
                                    )
                       .build();
    }
    
    private BigDecimal getAmount(Rental rental) {
        BigDecimal fee = rental.getCar().getDailyFee();
        long daysOfUse = ChronoUnit.DAYS.between(rental.getRentalDate(),
                                                rental.getActualReturnDate());
        BigDecimal amount = fee.multiply(BigDecimal.valueOf(daysOfUse))
                                    .multiply(BigDecimal.valueOf(100));
        if (rental.getActualReturnDate().isAfter(rental.getReturnDate())) {
            long daysOfOverdue = ChronoUnit.DAYS.between(rental.getReturnDate(),
                                                         rental.getActualReturnDate());
            return amount.add(BigDecimal.valueOf(daysOfOverdue).multiply(FINE_MULTIPLIER));
        } else {
            return amount;
        }
    }
}
