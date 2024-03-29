package com.github.yuliyadzemidovich.parceldeliveryapp.controller;

import com.github.yuliyadzemidovich.parceldeliveryapp.dto.UserDto;
import com.github.yuliyadzemidovich.parceldeliveryapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.github.yuliyadzemidovich.parceldeliveryapp.Constants.API_VERSION;
import static com.github.yuliyadzemidovich.parceldeliveryapp.Constants.REGISTER;
import static com.github.yuliyadzemidovich.parceldeliveryapp.Constants.USER;

@RestController
@RequestMapping(value = API_VERSION + USER, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(value = REGISTER, consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserDto register(@RequestBody @Valid UserDto userDto) {
        return userService.createUser(userDto);
    }
}
