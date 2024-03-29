package com.github.yuliyadzemidovich.parceldeliveryapp.controller;

import com.github.yuliyadzemidovich.parceldeliveryapp.service.HelloService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.github.yuliyadzemidovich.parceldeliveryapp.Constants.ADMIN;
import static com.github.yuliyadzemidovich.parceldeliveryapp.Constants.API_VERSION;
import static com.github.yuliyadzemidovich.parceldeliveryapp.Constants.USER;

/**
 * Dummy controller for demo and testing purposes.
 */
@RestController
@RequestMapping(API_VERSION + "/hello")
@RequiredArgsConstructor
public class HelloController {

    private final HelloService helloService;

    /**
     * Example of endpoint that only authenticated user should be allowed to call.
     * Should throw 4xx for unauthenticated user.
     */
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','USER')")
    @GetMapping(USER)
    public String helloUser() {
        return helloService.greetUser();
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @GetMapping(ADMIN)
    public String helloAdmin() {
        return helloService.greetAdmin();
    }
}
