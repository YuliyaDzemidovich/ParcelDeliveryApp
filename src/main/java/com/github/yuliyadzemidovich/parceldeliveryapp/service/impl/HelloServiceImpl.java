package com.github.yuliyadzemidovich.parceldeliveryapp.service.impl;

import com.github.yuliyadzemidovich.parceldeliveryapp.service.HelloService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class HelloServiceImpl implements HelloService {

    @PreAuthorize("hasRole('USER')")
    @Override
    public String greetUser() {
        return "hello authenticated user";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public String greetAdmin() {
        return "hello authenticated admin";
    }
}
