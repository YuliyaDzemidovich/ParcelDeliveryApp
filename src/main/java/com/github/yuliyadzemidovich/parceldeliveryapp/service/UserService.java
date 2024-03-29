package com.github.yuliyadzemidovich.parceldeliveryapp.service;

import com.github.yuliyadzemidovich.parceldeliveryapp.dto.UserDto;
import org.springframework.stereotype.Service;

/**
 * Service to manage Users.
 */
@Service
public interface UserService {

    /**
     * Create a new user with role USER
     * @param userDto dto with new user info
     * @return DTO of created user
     */
    UserDto createUser(UserDto userDto);

    /**
     * Admin can create a new user with role COURIER
     * @param userDto dto with new user info
     * @return DTO of created user
     */
    UserDto createCourier(UserDto userDto);

    /**
     * Admin can create a new user with role ADMIN
     * @param userDto dto with new user info
     * @return DTO of created user
     */
    UserDto createAdmin(UserDto userDto);
}
