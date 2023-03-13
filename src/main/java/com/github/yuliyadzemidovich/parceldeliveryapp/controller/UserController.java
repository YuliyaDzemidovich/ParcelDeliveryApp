package com.github.yuliyadzemidovich.parceldeliveryapp.controller;

import com.github.yuliyadzemidovich.parceldeliveryapp.exception.ParcelDeliveryAppException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.github.yuliyadzemidovich.parceldeliveryapp.Constants.API_VERSION;
import static com.github.yuliyadzemidovich.parceldeliveryapp.Constants.USER;

@RestController
@RequestMapping(API_VERSION + USER)
public class UserController {

    @PostMapping("/register")
    public void register() throws ParcelDeliveryAppException {
        throw new ParcelDeliveryAppException("not implemented");
    }
}
