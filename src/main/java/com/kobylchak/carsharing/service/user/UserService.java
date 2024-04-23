package com.kobylchak.carsharing.service.user;

import com.kobylchak.carsharing.dto.role.UpdateRoleRequestDto;
import com.kobylchak.carsharing.dto.user.UpdateUserInfoRequestDto;
import com.kobylchak.carsharing.dto.user.UserRegistrationRequestDto;
import com.kobylchak.carsharing.dto.user.UserResponseDto;
import com.kobylchak.carsharing.exception.RoleNotValidException;
import com.kobylchak.carsharing.exception.UserRegistrationException;
import com.kobylchak.carsharing.model.User;

public interface UserService {
    UserResponseDto registerUser(UserRegistrationRequestDto requestDto) throws
            UserRegistrationException;
    
    void updateUserRole(Long userId, UpdateRoleRequestDto requestDto) throws RoleNotValidException;
    
    UserResponseDto getUserInfo(User user);
    
    UserResponseDto updateUserInfo(User user, UpdateUserInfoRequestDto requestDto);
}
