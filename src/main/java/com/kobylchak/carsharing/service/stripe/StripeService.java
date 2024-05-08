package com.kobylchak.carsharing.service.stripe;

import com.kobylchak.carsharing.model.User;
import com.stripe.model.checkout.Session;

public interface StripeService {
    Session createSession(User user);
}
