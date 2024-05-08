package com.kobylchak.carsharing.repository.payment;

import com.kobylchak.carsharing.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
