package com.github.yuliyadzemidovich.parceldeliveryapp.service.impl;

import com.github.yuliyadzemidovich.parceldeliveryapp.dto.OrderDto;
import com.github.yuliyadzemidovich.parceldeliveryapp.entity.Delivery;
import com.github.yuliyadzemidovich.parceldeliveryapp.entity.Order;
import com.github.yuliyadzemidovich.parceldeliveryapp.entity.Parcel;
import com.github.yuliyadzemidovich.parceldeliveryapp.entity.User;
import com.github.yuliyadzemidovich.parceldeliveryapp.exception.WebException;
import com.github.yuliyadzemidovich.parceldeliveryapp.repository.OrderRepository;
import com.github.yuliyadzemidovich.parceldeliveryapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static com.github.yuliyadzemidovich.parceldeliveryapp.service.impl.OrderServiceImpl.ORDER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepo;
    @Mock
    private UserRepository userRepo;
    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void getOrderByIdNotFound() {
        long orderId = 1;
        WebException ex = assertThrows(
                WebException.class,
                () -> orderService.getOrderById(orderId)
        );
        assertTrue(ex.getMessage().contentEquals(String.format(ORDER_NOT_FOUND, orderId)));
    }

    @Test
    void getOrderById() {
        Order existingOrder = getExistingOrder();
        long orderId = existingOrder.getId();
        when(orderRepo.findById(orderId)).thenReturn(Optional.of(existingOrder));

        // mock spring auth context
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // mock fetching authed user
        User mockedAuthedUser = getAuthedUser();
        when(userRepo.findByEmail(any())).thenReturn(mockedAuthedUser);

        // order belongs to the authed user
        existingOrder.setSender(mockedAuthedUser);

        // method under test
        OrderDto fetchedOrderDto = orderService.getOrderById(orderId);
        assertNotNull(fetchedOrderDto);
        assertEquals(orderId, fetchedOrderDto.getId());
    }

    private User getAuthedUser() {
        long userId = 1;
        User user = new User();
        user.setId(userId);
        return user;
    }

    private Order getExistingOrder() {
        long orderId = 1;
        Order order = new Order();
        order.setId(orderId);
        order.setParcel(new Parcel());
        order.setDelivery(new Delivery());
        return order;
    }
}
