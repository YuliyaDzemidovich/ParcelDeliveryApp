package com.github.yuliyadzemidovich.parceldeliveryapp.service.impl;

import com.github.yuliyadzemidovich.parceldeliveryapp.service.HelloService;
import org.springframework.stereotype.Service;

@Service
public class HelloServiceImpl implements HelloService {

    @Override
    public String greetUser() {
        return "hello authenticated user";
    }

    @Override
    public String greetAdmin() {
        return "hello authenticated admin";
    }
}
