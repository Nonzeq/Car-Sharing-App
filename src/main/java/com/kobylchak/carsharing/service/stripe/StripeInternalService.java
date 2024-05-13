package com.kobylchak.carsharing.service.stripe;

import com.kobylchak.carsharing.model.Payment;
import com.kobylchak.carsharing.model.User;
import com.stripe.model.checkout.Session;

public interface StripeInternalService {
    Session createSession(User user, Payment payment);
    
    boolean checkSuccess(String sessionId);
    
    boolean checkCancel(String sessionId);
    
    Session getSessionById(String sessionId);
}
