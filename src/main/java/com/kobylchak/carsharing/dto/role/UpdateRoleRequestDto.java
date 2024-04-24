package com.kobylchak.carsharing.dto.role;

import com.kobylchak.carsharing.model.enums.UserRole;
import com.kobylchak.carsharing.validation.car.EnumType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateRoleRequestDto {
    @NotBlank
    @EnumType(type = UserRole.class)
    private String roleName;
}
