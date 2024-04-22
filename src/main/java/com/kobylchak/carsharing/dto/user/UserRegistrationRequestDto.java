package com.kobylchak.carsharing.dto.user;

import lombok.Data;

@Data
public class UserRegistrationRequestDto {
    private String email;
    private String password;
    private String repeatPassword;
    private String firstName;
    private String lastName;
}
