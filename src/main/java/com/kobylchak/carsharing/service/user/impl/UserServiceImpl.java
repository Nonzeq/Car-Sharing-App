package com.kobylchak.carsharing.service.user.impl;

import com.kobylchak.carsharing.dto.role.UpdateRoleRequestDto;
import com.kobylchak.carsharing.dto.user.UpdateUserInfoRequestDto;
import com.kobylchak.carsharing.dto.user.UserRegistrationRequestDto;
import com.kobylchak.carsharing.dto.user.UserResponseDto;
import com.kobylchak.carsharing.exception.RoleNotValidException;
import com.kobylchak.carsharing.exception.UserRegistrationException;
import com.kobylchak.carsharing.mapper.user.UserMapper;
import com.kobylchak.carsharing.model.Role;
import com.kobylchak.carsharing.model.User;
import com.kobylchak.carsharing.model.enums.UserRole;
import com.kobylchak.carsharing.repository.role.RoleRepository;
import com.kobylchak.carsharing.repository.user.UserReposiotry;
import com.kobylchak.carsharing.service.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserReposiotry userReposiotry;
    private final RoleRepository roleRepository;
    
    @Override
    public UserResponseDto registerUser(UserRegistrationRequestDto requestDto) throws
            UserRegistrationException {
        if (userReposiotry.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new UserRegistrationException("Email is already in use");
        }
        Role defaultRole = roleRepository.findByName(UserRole.CUSTOMER)
                                         .orElseThrow(
                                                 () -> new UserRegistrationException(
                                                         "Default role is not found"));
        User user = userMapper.toModel(
                requestDto,
                passwordEncoder.encode(requestDto.getPassword()),
                defaultRole);
        return userMapper.toDto(userReposiotry.save(user));
    }
    
    @Override
    @Transactional
    public UserResponseDto updateUserRole(Long userId, UpdateRoleRequestDto requestDto)
            throws RoleNotValidException {
        Role role = roleRepository.findByName(UserRole.valueOf(requestDto.getRoleName()))
                                  .orElseThrow(
                                          () -> new RoleNotValidException("Role is not valid"));
        User user = userReposiotry.findById(userId)
                                  .orElseThrow(
                                          () -> new EntityNotFoundException("User not found"));
        user.setRole(role);
        return userMapper.toDto(userReposiotry.save(user));
    }
    
    @Override
    public UserResponseDto getUserInfo(User user) {
        return userMapper.toDto(user);
    }
    
    @Override
    public UserResponseDto updateUserInfo(User user, UpdateUserInfoRequestDto requestDto) {
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
        return userMapper.toDto(userReposiotry.save(user));
    }
}
