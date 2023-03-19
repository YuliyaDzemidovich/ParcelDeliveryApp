package com.github.yuliyadzemidovich.parceldeliveryapp.service;

import com.github.yuliyadzemidovich.parceldeliveryapp.dto.UserDto;
import org.springframework.stereotype.Service;

/**
 * Service to manage Users.
 */
@Service
public interface UserService {

    /**
     * Create a new user
     * @param userDto dto with new user info
     * @return DTO of created user
     */
    UserDto createUser(UserDto userDto);
}
