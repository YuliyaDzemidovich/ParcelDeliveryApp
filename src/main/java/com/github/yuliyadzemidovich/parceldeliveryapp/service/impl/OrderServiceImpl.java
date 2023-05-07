package com.github.yuliyadzemidovich.parceldeliveryapp.service.impl;

import com.github.yuliyadzemidovich.parceldeliveryapp.dto.OrderDto;
import com.github.yuliyadzemidovich.parceldeliveryapp.service.OrderService;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {
    @Override
    public OrderDto createOrder(OrderDto orderDto) {
        // todo implement
        return OrderDto.builder().data("not implemented yet, but your request is: " + orderDto.getData()).build();
    }
}
