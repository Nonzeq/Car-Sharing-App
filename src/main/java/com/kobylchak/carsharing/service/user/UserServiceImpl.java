package com.kobylchak.carsharing.service.user;

import com.kobylchak.carsharing.dto.user.UserRegistrationRequestDto;
import com.kobylchak.carsharing.dto.user.UserResponseDto;
import com.kobylchak.carsharing.exception.UserRegistrationException;
import com.kobylchak.carsharing.mapper.user.UserMapper;
import com.kobylchak.carsharing.model.Role;
import com.kobylchak.carsharing.model.User;
import com.kobylchak.carsharing.repository.role.RoleRepository;
import com.kobylchak.carsharing.repository.user.UserReposiotry;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        Role defaultRole = roleRepository.findByName(Role.UserRole.CUSTOMER.name())
                                                    .orElseThrow(
                                                            () -> new UserRegistrationException(
                                                                    "Default role is not found"));
        User user = userMapper.toModel(
                requestDto,
                passwordEncoder.encode(requestDto.getPassword()),
                defaultRole);
        return userMapper.toDto(userReposiotry.save(user));
    }
}
