package com.kobylchak.carsharing.controller;

import com.kobylchak.carsharing.dto.role.UpdateRoleRequestDto;
import com.kobylchak.carsharing.dto.user.UpdateUserInfoRequestDto;
import com.kobylchak.carsharing.dto.user.UserResponseDto;
import com.kobylchak.carsharing.model.User;
import com.kobylchak.carsharing.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    
    @PutMapping("/{userId}/role")
    @PreAuthorize("hasRole('MANAGER')")
    public void updateUserRole(@PathVariable Long userId,
                                          @RequestBody UpdateRoleRequestDto requestDto) {
        userService.updateUserRole(userId, requestDto);
    }
    
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('MANAGER', 'CUSTOMER')")
    public UserResponseDto getCurrentUserInfo(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return userService.getUserInfo(user);
    }
    
    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('MANAGER', 'CUSTOMER')")
    public UserResponseDto updateUserInfo(Authentication authentication,
                                      @RequestBody @Valid UpdateUserInfoRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        return userService.updateUserInfo(user, requestDto);
    }
    
}
