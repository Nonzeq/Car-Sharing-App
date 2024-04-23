package com.kobylchak.carsharing.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UpdateUserInfoRequestDto {
    @NotBlank
    @Length(min = 2, max = 30)
    private String firstName;
    @NotBlank
    @Length(min = 2, max = 30)
    private String lastName;
}
