package com.kobylchak.carsharing.repository.rental;

import com.kobylchak.carsharing.model.Rental;
import com.kobylchak.carsharing.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RentalRepository extends JpaRepository<Rental, Long>,
                                          JpaSpecificationExecutor<Rental> {
    Optional<Rental> findByUserAndId(User user, Long id);
}
