package com.kobylchak.carsharing.repository.user;

import com.kobylchak.carsharing.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("from User u join fetch u.role where u.email = :email")
    Optional<User> findByEmailWithRoles(String email);
    
    Optional<User> findByEmail(String email);
}
