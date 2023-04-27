package com.github.yuliyadzemidovich.parceldeliveryapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.github.yuliyadzemidovich.parceldeliveryapp.Constants.API_VERSION;

/**
 * Dummy controller for demo and testing purposes.
 */
@RestController
@RequestMapping(API_VERSION + "/hello")
public class HelloController {

    /**
     * Example of endpoint that only authenticated user should be allowed to call.
     * Should throw 4xx for unauthenticated user.
     */
    @GetMapping
    public String hello() {
        return "hello authenticated user";
    }
}
