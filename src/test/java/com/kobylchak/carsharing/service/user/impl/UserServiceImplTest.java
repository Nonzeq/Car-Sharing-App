package com.kobylchak.carsharing.service.user.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.kobylchak.carsharing.dto.role.UpdateRoleRequestDto;
import com.kobylchak.carsharing.dto.user.UserRegistrationRequestDto;
import com.kobylchak.carsharing.dto.user.UserResponseDto;
import com.kobylchak.carsharing.exception.RoleNotValidException;
import com.kobylchak.carsharing.exception.UserRegistrationException;
import com.kobylchak.carsharing.mapper.UserMapper;
import com.kobylchak.carsharing.model.Role;
import com.kobylchak.carsharing.model.User;
import com.kobylchak.carsharing.model.enums.UserRole;
import com.kobylchak.carsharing.repository.role.RoleRepository;
import com.kobylchak.carsharing.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserRepository userReposiotry;
    @Mock
    private RoleRepository roleRepository;
    
    @Test
    public void registerUser_ValidParameters_ShouldReturnUserDto() {
        final String password = "password";
        final User user = getUser();
        user.setId(1L);
        final Role customerRole = getCustomerRole();
        final UserRole userRole = UserRole.CUSTOMER;
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setEmail(user.getEmail());
        requestDto.setFirstName(user.getFirstName());
        requestDto.setLastName(user.getLastName());
        requestDto.setPassword(password);
        requestDto.setRepeatPassword(password);
        
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setEmail(requestDto.getEmail());
        userResponseDto.setLastName(requestDto.getLastName());
        userResponseDto.setFirstName(requestDto.getFirstName());
        userResponseDto.setId(user.getId());
        
        when(userReposiotry.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.findByName(userRole)).thenReturn(Optional.of(customerRole));
        when(userMapper.toModel(requestDto, password, customerRole)).thenReturn(user);
        when(passwordEncoder.encode(password)).thenReturn(password);
        when(userReposiotry.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userResponseDto);
        
        UserResponseDto actual = userService.registerUser(requestDto);
        
        assertNotNull(actual);
        assertEquals(userResponseDto, actual);
    }
    
    @Test
    public void registerUser_EmailAlreadyExist_ShouldThrowUserRegistrationException() {
        final String password = "password";
        final User user = getUser();
        user.setId(1L);
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setEmail(user.getEmail());
        requestDto.setFirstName(user.getFirstName());
        requestDto.setLastName(user.getLastName());
        requestDto.setPassword(password);
        requestDto.setRepeatPassword(password);
        
        when(userReposiotry.findByEmail(requestDto.getEmail())).thenReturn(Optional.of(new User()));
        UserRegistrationException userRegistrationException = assertThrows(
                UserRegistrationException.class, () -> userService.registerUser(requestDto));
        String expectedMessage = "Email is already in use";
        assertNotNull(userRegistrationException);
        assertEquals(expectedMessage, userRegistrationException.getMessage());
    }
    
    @Test
    public void registerUser_CustomerRoleDoesNotExist_ShouldThrowUserRegistrationException() {
        final String password = "password";
        final User user = getUser();
        user.setId(1L);
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setEmail(user.getEmail());
        requestDto.setFirstName(user.getFirstName());
        requestDto.setLastName(user.getLastName());
        requestDto.setPassword(password);
        requestDto.setRepeatPassword(password);
        
        when(userReposiotry.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.findByName(UserRole.CUSTOMER)).thenReturn(Optional.empty());
        UserRegistrationException userRegistrationException = assertThrows(
                UserRegistrationException.class, () -> userService.registerUser(requestDto));
        String expectedMessage = "Default role is not found";
        assertNotNull(userRegistrationException);
        assertEquals(expectedMessage, userRegistrationException.getMessage());
    }
    
    @Test
    public void updateUserRole_ValidParameters_ShouldReturnUpdatedUserDto() {
        final Long userId = 1L;
        final UserRole roleName = UserRole.MANAGER;
        final Role roleManager = new Role();
        final Role roleCustomer = new Role();
        final User user = getUser();
        user.setRole(roleCustomer);
        roleCustomer.setName(UserRole.CUSTOMER);
        roleManager.setName(UserRole.MANAGER);
        UpdateRoleRequestDto updateRoleRequestDto = new UpdateRoleRequestDto();
        updateRoleRequestDto.setRoleName(roleName);
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setRole(roleManager.getName().name());
        
        when(roleRepository.findByName(roleName)).thenReturn(
                Optional.of(roleManager));
        when(userReposiotry.findById(userId)).thenReturn(Optional.of(user));
        when(userReposiotry.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userResponseDto);
        UserResponseDto updateUserRole = userService.updateUserRole(userId, updateRoleRequestDto);
        
        assertEquals(roleManager.getName().name(), updateUserRole.getRole());
    }
    
    @Test
    public void updateUserRole_InvalidUserId_ShouldThrowEntityNotFoundException() {
        final Long userId = 1L;
        final UserRole roleName = UserRole.MANAGER;
        final Role roleManager = new Role();
        final Role roleCustomer = new Role();
        final User user = getUser();
        user.setRole(roleCustomer);
        roleCustomer.setName(UserRole.CUSTOMER);
        roleManager.setName(UserRole.MANAGER);
        UpdateRoleRequestDto updateRoleRequestDto = new UpdateRoleRequestDto();
        updateRoleRequestDto.setRoleName(roleName);
        
        when(roleRepository.findByName(roleName))
                .thenReturn(Optional.of(new Role()));
        
        EntityNotFoundException roleNotValidException = assertThrows(
                EntityNotFoundException.class,
                () -> userService.updateUserRole(
                        userId,
                        updateRoleRequestDto));
        String expectedMessage = "User not found";
        assertNotNull(roleNotValidException);
        assertEquals(expectedMessage, roleNotValidException.getMessage());
    }
    
    @Test
    public void updateUserRole_InvalidRole_ShouldThrowRoleNotValidException() {
        final Long userId = 1L;
        final UserRole roleName = UserRole.MANAGER;
        final Role roleManager = new Role();
        final Role roleCustomer = new Role();
        final User user = getUser();
        user.setRole(roleCustomer);
        roleCustomer.setName(UserRole.CUSTOMER);
        roleManager.setName(UserRole.MANAGER);
        UpdateRoleRequestDto updateRoleRequestDto = new UpdateRoleRequestDto();
        updateRoleRequestDto.setRoleName(roleName);
        
        when(roleRepository.findByName(roleName))
                .thenReturn(Optional.empty());
        
        RoleNotValidException roleNotValidException = assertThrows(
                RoleNotValidException.class,
                () -> userService.updateUserRole(
                        userId,
                        updateRoleRequestDto));
        String expectedMessage = "Role is not valid";
        assertNotNull(roleNotValidException);
        assertEquals(expectedMessage, roleNotValidException.getMessage());
    }
    
    private Role getCustomerRole() {
        Role role = new Role();
        role.setName(UserRole.CUSTOMER);
        return role;
    }
    
    private User getUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setFirstName("test");
        user.setLastName("test");
        return user;
    }
}
