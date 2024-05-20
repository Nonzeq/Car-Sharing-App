package com.kobylchak.carsharing.dto.role;

import com.kobylchak.carsharing.model.enums.UserRole;
import com.kobylchak.carsharing.validation.annotation.EnumType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateRoleRequestDto {
    @NotNull
    @EnumType(type = UserRole.class)
    private UserRole roleName;
}
