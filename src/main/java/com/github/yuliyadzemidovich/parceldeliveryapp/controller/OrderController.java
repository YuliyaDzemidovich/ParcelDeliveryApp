package com.github.yuliyadzemidovich.parceldeliveryapp.controller;

import com.github.yuliyadzemidovich.parceldeliveryapp.dto.OrderDto;
import com.github.yuliyadzemidovich.parceldeliveryapp.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.github.yuliyadzemidovich.parceldeliveryapp.Constants.ADMIN;
import static com.github.yuliyadzemidovich.parceldeliveryapp.Constants.API_VERSION;
import static com.github.yuliyadzemidovich.parceldeliveryapp.Constants.CANCEL;
import static com.github.yuliyadzemidovich.parceldeliveryapp.Constants.CANCEL_ALL;
import static com.github.yuliyadzemidovich.parceldeliveryapp.Constants.ORDERS;
import static com.github.yuliyadzemidovich.parceldeliveryapp.Constants.PARAM_ORDER_ID;
import static com.github.yuliyadzemidovich.parceldeliveryapp.Constants.USER;

@RestController
@RequestMapping(value = API_VERSION, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','USER')")
    @PostMapping(path =  USER + ORDERS, consumes = MediaType.APPLICATION_JSON_VALUE)
    public OrderDto createOrder(@RequestBody @Valid OrderDto orderDto) {
        return orderService.createOrder(orderDto);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping(path = USER + ORDERS + PARAM_ORDER_ID)
    public OrderDto updateDeliveryAddress(@PathVariable long orderId,
                                          @RequestBody @NotBlank @Size(max = 255) String newAddress) {
        return orderService.updateDeliveryAddress(orderId, newAddress);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping(path =  USER + ORDERS)
    public List<OrderDto> getUserOrders() {
        return orderService.getUserOrders();
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping(path =  ADMIN + ORDERS)
    public List<OrderDto> getAllOrders() {
        return orderService.getAllOrders();
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    @GetMapping(path = USER + ORDERS + PARAM_ORDER_ID)
    public OrderDto getOrderById(@PathVariable long orderId) {
        return orderService.getOrderById(orderId);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping(path = USER + ORDERS + CANCEL)
    public OrderDto cancelOrder(@RequestParam long orderId) {
        return orderService.cancelOrder(orderId);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping(path = USER + ORDERS + CANCEL_ALL)
    public void cancelAllOrder() {
        orderService.cancelAllOrders();
    }
}
