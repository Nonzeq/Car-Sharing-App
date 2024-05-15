package com.kobylchak.carsharing.repository.rental;

import com.kobylchak.carsharing.model.Rental;
import com.kobylchak.carsharing.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface RentalRepository extends JpaRepository<Rental, Long>,
                                          JpaSpecificationExecutor<Rental> {
    Optional<Rental> findByUserAndId(User user, Long id);
    
    @Query("from Rental rental "
               + "join fetch rental.car "
               + "join fetch rental.user "
               + "where rental.returnDate < current date and rental.actualReturnDate is null")
    List<Rental> findAllOverdueRentals();
    
    List<Rental> findAllByUserId(Long userId);
    
    @Query("from Rental r join fetch r.user u where u.id = :userId and r.isActive = true")
    List<Rental> findAllByUserIdAndIsActive(Long userId);
    
    @Query("from Rental rental where rental.id = :id and rental.actualReturnDate is not null")
    Optional<Rental> findByIdAndActualReturnDateIsExist(Long id);
}
