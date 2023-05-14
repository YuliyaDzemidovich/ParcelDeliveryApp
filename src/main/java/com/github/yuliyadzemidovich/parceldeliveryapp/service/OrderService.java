package com.github.yuliyadzemidovich.parceldeliveryapp.service;

import com.github.yuliyadzemidovich.parceldeliveryapp.dto.OrderDto;

import java.util.List;

public interface OrderService {

    /**
     * User can create a new order.
     * @param orderDto new order request
     * @return created order
     */
    OrderDto createOrder(OrderDto orderDto);

    /**
     * Get all orders for currently authenticated user
     * @return list of orders
     */
    List<OrderDto> getUserOrders();
}
