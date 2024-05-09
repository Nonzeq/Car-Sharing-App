package com.kobylchak.carsharing.service.stripe;

import com.kobylchak.carsharing.model.Rental;
import com.kobylchak.carsharing.model.User;
import com.stripe.model.checkout.Session;

public interface StripeInternalService {
    Session createSession(User user, Rental rental);
    
    boolean checkSuccess(String sessionId);
}
