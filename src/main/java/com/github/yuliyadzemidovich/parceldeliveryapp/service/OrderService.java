package com.github.yuliyadzemidovich.parceldeliveryapp.service;

import com.github.yuliyadzemidovich.parceldeliveryapp.dto.OrderDto;

public interface OrderService {

    /**
     * User can create a new order.
     * @param orderDto new order request
     * @return created order
     */
    OrderDto createOrder(OrderDto orderDto);
}
