package com.kobylchak.carsharing.service.user;

import com.kobylchak.carsharing.dto.user.UserRegistrationRequestDto;
import com.kobylchak.carsharing.dto.user.UserResponseDto;
import com.kobylchak.carsharing.exception.UserRegistrationException;

public interface UserService {
    UserResponseDto registerUser(UserRegistrationRequestDto requestDto) throws
            UserRegistrationException;
}
