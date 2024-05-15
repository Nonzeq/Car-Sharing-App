package com.kobylchak.carsharing.controller;

import com.kobylchak.carsharing.dto.role.UpdateRoleRequestDto;
import com.kobylchak.carsharing.dto.user.UpdateUserInfoRequestDto;
import com.kobylchak.carsharing.dto.user.UserResponseDto;
import com.kobylchak.carsharing.model.User;
import com.kobylchak.carsharing.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(
        name = "Users management",
        description = "Endpoints for users management"
)
public class UserController {
    private final UserService userService;
    
    @PutMapping("/{userId}/role")
    @PreAuthorize("hasRole('MANAGER')")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update user role by id")
    public void updateUserRole(@PathVariable Long userId,
                                          @RequestBody @Valid UpdateRoleRequestDto requestDto) {
        userService.updateUserRole(userId, requestDto);
    }
    
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('MANAGER', 'CUSTOMER')")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get user user info for the current authorized user")
    public UserResponseDto getCurrentUserInfo(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return userService.getUserInfo(user);
    }
    
    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('MANAGER', 'CUSTOMER')")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update user info")
    public UserResponseDto updateUserInfo(Authentication authentication,
                                      @RequestBody @Valid UpdateUserInfoRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        return userService.updateUserInfo(user, requestDto);
    }
    
}
