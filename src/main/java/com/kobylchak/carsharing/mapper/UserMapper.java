package com.kobylchak.carsharing.mapper;

import com.kobylchak.carsharing.config.MapperConfig;
import com.kobylchak.carsharing.dto.user.UserRegistrationRequestDto;
import com.kobylchak.carsharing.dto.user.UserResponseDto;
import com.kobylchak.carsharing.model.Role;
import com.kobylchak.carsharing.model.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    @Mapping(target = "password", ignore = true)
    User toModel(
            UserRegistrationRequestDto requestDto,
            @Context String hashedPassword,
            @Context Role defaultRole);
    
    @AfterMapping
    default void setHashedPassword(@MappingTarget User user, @Context String hashedPassword) {
        user.setPassword(hashedPassword);
    }
    
    @AfterMapping
    default void setDefaultRole(@MappingTarget User user, @Context Role defaultRole) {
        user.setRole(defaultRole);
    }
    
    @Mapping(target = "role", source = "role.name")
    UserResponseDto toDto(User user);
}
