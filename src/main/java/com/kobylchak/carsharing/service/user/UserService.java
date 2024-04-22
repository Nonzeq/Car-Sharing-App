package com.kobylchak.carsharing.service.user;

import com.kobylchak.carsharing.dto.user.UserRegistrationRequestDto;
import com.kobylchak.carsharing.dto.user.UserResponseDto;

public interface UserService {
    UserResponseDto registerUser(UserRegistrationRequestDto requestDto);
}
