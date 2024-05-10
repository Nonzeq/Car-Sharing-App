package com.kobylchak.carsharing.repository.payment;

import com.kobylchak.carsharing.model.Payment;
import com.kobylchak.carsharing.model.enums.PaymentType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    @Query("from Payment payment join fetch payment.rental r where payment.sessionId = :sessionId")
    Optional<Payment> findBySessionId(String sessionId);
    
    Optional<Payment> findByRentalIdAndType(Long rentalId, PaymentType type);
}
