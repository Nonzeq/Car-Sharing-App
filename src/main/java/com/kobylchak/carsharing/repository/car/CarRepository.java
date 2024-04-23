package com.kobylchak.carsharing.repository.car;

import com.kobylchak.carsharing.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {
}
