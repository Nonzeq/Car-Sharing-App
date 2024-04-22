package com.kobylchak.carsharing.service.user;

import com.kobylchak.carsharing.dto.user.UserRegistrationRequestDto;
import com.kobylchak.carsharing.dto.user.UserResponseDto;
import com.kobylchak.carsharing.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    @Override
    public UserResponseDto registerUser(UserRegistrationRequestDto requestDto) {
        User user = new User();
        
        return null;
    }
}
