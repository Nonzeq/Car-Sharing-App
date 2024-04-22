package com.kobylchak.carsharing.controller;

import com.kobylchak.carsharing.dto.user.UserLoginRequestDto;
import com.kobylchak.carsharing.dto.user.UserLoginResponseDto;
import com.kobylchak.carsharing.dto.user.UserRegistrationRequestDto;
import com.kobylchak.carsharing.dto.user.UserResponseDto;
import com.kobylchak.carsharing.service.auth.AuthenticationService;
import com.kobylchak.carsharing.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {
    private final AuthenticationService authenticationService;
    private final UserService userService;
    
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public UserLoginResponseDto login(@RequestBody UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }
    
    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto register(
            @RequestBody UserRegistrationRequestDto requestDto) {
        return userService.registerUser(requestDto);
    }

}
