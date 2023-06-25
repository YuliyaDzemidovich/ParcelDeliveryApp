package com.github.yuliyadzemidovich.parceldeliveryapp.service;

import com.github.yuliyadzemidovich.parceldeliveryapp.dto.OrderDto;
import com.github.yuliyadzemidovich.parceldeliveryapp.entity.Order;
import com.github.yuliyadzemidovich.parceldeliveryapp.exception.WebException;

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

    /**
     * Get all orders
     * @return list of orders
     */
    List<OrderDto> getAllOrders();

    /**
     * Try cancel user's order by order ID
     * @param orderId order ID
     * @return cancelled order if was successfully cancelled
     * @throws WebException if unable to cancel order
     */
    OrderDto cancelOrder(long orderId);

    /**
     * Defines business rule if order can be canceled or not.
     * @param order order to be canceled
     * @return true if order can be canceled, false otherwise
     */
    boolean canBeCanceled(Order order);

    /**
     * Get order by ID. For USER role, order must belong to the authorized user.
     * @param orderId order ID
     * @return found order
     */
    OrderDto getOrderById(long orderId);

    /**
     * Try cancel all orders of currently authenticated user.
     * All orders must belong to the authenticated user.
     * @throws WebException if unable to cancel orders
     */
    void cancelAllOrders();

    /**
     * Update delivery address for existing order
     * @param orderId order ID
     * @param newAddress new address
     * @return updated order
     */
    OrderDto updateDeliveryAddress(long orderId, String newAddress);
}
