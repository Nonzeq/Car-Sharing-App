package com.kobylchak.carsharing.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "rentals")
@Getter
@Setter
@SQLRestriction("is_deleted=FALSE")
@SQLDelete(sql = "UPDATE rentals set is_deleted = true WHERE id=?")
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, name = "rental_date")
    private LocalDate rentalDate = LocalDate.now();
    
    @Column(name = "return_date")
    private LocalDate returnDate;
    
    @Column(name = "actual_return_date")
    private LocalDate actualReturnDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "car_id")
    private Car car;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;
    
    @Column(nullable = false, name = "is_deleted")
    private boolean isDeleted = false;
    
    @Column(nullable = false, name = "is_active")
    private boolean isActive = true;
}
