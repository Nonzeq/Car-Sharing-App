package com.kobylchak.carsharing.repository.role;

import com.kobylchak.carsharing.model.Role;
import com.kobylchak.carsharing.model.enums.UserRole;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(UserRole name);
}
