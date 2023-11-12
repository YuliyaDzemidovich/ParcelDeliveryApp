package com.github.yuliyadzemidovich.parceldeliveryapp.controller;

import com.github.yuliyadzemidovich.parceldeliveryapp.dto.CourierDto;
import com.github.yuliyadzemidovich.parceldeliveryapp.dto.UserDto;
import com.github.yuliyadzemidovich.parceldeliveryapp.service.CourierService;
import com.github.yuliyadzemidovich.parceldeliveryapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.github.yuliyadzemidovich.parceldeliveryapp.Constants.ADMIN;
import static com.github.yuliyadzemidovich.parceldeliveryapp.Constants.API_VERSION;
import static com.github.yuliyadzemidovich.parceldeliveryapp.Constants.COURIER;
import static com.github.yuliyadzemidovich.parceldeliveryapp.Constants.COURIERS;
import static com.github.yuliyadzemidovich.parceldeliveryapp.Constants.REGISTER;

@RestController
@RequestMapping(value = API_VERSION, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CourierController {
    private final UserService userService;
    private final CourierService courierService;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PostMapping(value = COURIER + REGISTER, consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserDto register(@RequestBody @Valid UserDto userDto) {
        return userService.createCourier(userDto);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping(path = ADMIN + COURIERS)
    public List<CourierDto> getAllCouriers() {
        return courierService.getAllCouriers();
    }

    // todo courier needs a "I'm available for work" method - that fills CourierInfo table - so that admin can see courier's availability
}
