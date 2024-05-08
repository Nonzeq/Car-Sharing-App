package com.kobylchak.carsharing.model;

import com.kobylchak.carsharing.model.enums.PaymentStatus;
import com.kobylchak.carsharing.model.enums.PaymentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.net.URL;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "payments")
@Getter
@Setter
@SQLRestriction("is_deleted=FALSE")
@SQLDelete(sql = "UPDATE payments set is_deleted = true WHERE id=?")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar")
    private PaymentStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar")
    private PaymentType type;
    
    @OneToOne
    @JoinColumn(name = "rental_id")
    private Rental rental;
    
    @Column(nullable = false, name = "session_url")
    private URL sessionURL;
    
    @Column(nullable = false, name = "session_id")
    private String sessionId;
    
    @Column(nullable = false, name = "amount_to_pay")
    private BigDecimal amountToPay;
}
