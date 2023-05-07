package com.github.yuliyadzemidovich.parceldeliveryapp.controller;

import com.github.yuliyadzemidovich.parceldeliveryapp.dto.OrderDto;
import com.github.yuliyadzemidovich.parceldeliveryapp.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.github.yuliyadzemidovich.parceldeliveryapp.Constants.API_VERSION;
import static com.github.yuliyadzemidovich.parceldeliveryapp.Constants.ORDERS;

@RestController
@RequestMapping(value = API_VERSION + ORDERS, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','USER')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public OrderDto createOrder(@RequestBody @Valid OrderDto orderDto) {
        return orderService.createOrder(orderDto);
    }
}

