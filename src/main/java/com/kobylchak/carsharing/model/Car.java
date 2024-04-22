package com.kobylchak.carsharing.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "cars")
@Getter
@Setter
@SQLRestriction("is_deleted=FALSE")
@SQLDelete(sql = "UPDATE cars set is_deleted = true WHERE id=?")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String brand;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar")
    private CarType type;

    private int inventory;

    @Column(name = "daily_fee")
    private BigDecimal dailyFee;

    @Column(nullable = false, name = "is_deleted")
    private boolean isDeleted = false;

    public enum CarType {
        SEDAN,
        SUV,
        HATCHBACK,
        UNIVERSAL
    }
}
