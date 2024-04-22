package com.kobylchak.carsharing.service.auth;

import com.kobylchak.carsharing.dto.user.UserLoginRequestDto;
import com.kobylchak.carsharing.dto.user.UserLoginResponseDto;

public interface AuthenticationService {
    UserLoginResponseDto authenticate(UserLoginRequestDto requestDto);
}
