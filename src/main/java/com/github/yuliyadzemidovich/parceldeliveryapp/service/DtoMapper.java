package com.github.yuliyadzemidovich.parceldeliveryapp.service;

import com.github.yuliyadzemidovich.parceldeliveryapp.dto.UserDto;
import com.github.yuliyadzemidovich.parceldeliveryapp.entity.User;

/**
 * Mapper between DTO and entity classes.
 */
public class DtoMapper {

    private DtoMapper() {}

    public static User mapToUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        // no mapping for role - dto should not be able to enumerate existing roles. Roles should be handled by service class
        return user;
    }

    public static UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                // no mapping for password hash
                .role(user.getRole().getShortValue())
                .build();
    }
}
