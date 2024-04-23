package com.kobylchak.carsharing.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UserLoginRequestDto {
    @Email
    @NotBlank
    @Length(min = 8, max = 30)
    private String email;
    @NotBlank
    @Length(min = 8, max = 30)
    private String password;
}
