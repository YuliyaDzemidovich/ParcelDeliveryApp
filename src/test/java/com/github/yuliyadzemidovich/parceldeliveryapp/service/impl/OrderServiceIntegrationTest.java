package com.github.yuliyadzemidovich.parceldeliveryapp.service.impl;

import com.github.yuliyadzemidovich.parceldeliveryapp.dto.DeliveryDto;
import com.github.yuliyadzemidovich.parceldeliveryapp.dto.OrderDto;
import com.github.yuliyadzemidovich.parceldeliveryapp.dto.ParcelDto;
import com.github.yuliyadzemidovich.parceldeliveryapp.entity.OrderStatus;
import com.github.yuliyadzemidovich.parceldeliveryapp.entity.Role;
import com.github.yuliyadzemidovich.parceldeliveryapp.entity.User;
import com.github.yuliyadzemidovich.parceldeliveryapp.repository.UserRepository;
import com.github.yuliyadzemidovich.parceldeliveryapp.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for Service layer. Uses H2 in-memory database, Spring container - spring beans, JPA repositories.
 */
@SpringBootTest
@AutoConfigureTestDatabase // use in-memory H2
class OrderServiceIntegrationTest {

    @Autowired
    UserRepository userRepo;

    @Autowired
    private OrderService orderService;

    @Test
    void createOrder() {
        // pre-create existing user
        final long SENDER_ID = 1L;
        User sender = new User();
        sender.setId(SENDER_ID);
        sender.setName("Bob");
        sender.setRole(Role.ROLE_USER);
        sender.setEmail("bob@email.com");
        sender.setPassword("pw_hash");
        userRepo.save(sender);

        // prepare new order request
        ParcelDto parcelDto = ParcelDto.builder()
                .weight(new BigDecimal(5))
                .build();
        DeliveryDto deliveryDto = DeliveryDto.builder()
                .pickupLatitude("43.69364466860889")
                .pickupLongitude("32.05835110656418")
                .build();
        OrderDto orderReq = OrderDto.builder()
                .receiverName("Alice")
                .receiverAddress("505 North Street")
                .receiverPhone("555-789-041")
                .parcelDto(parcelDto)
                .deliveryDto(deliveryDto)
                .senderId(SENDER_ID)
                .build();

        // method under test
        OrderDto createdOrder = orderService.createOrder(orderReq);
        assertNotNull(createdOrder);
        assertTrue(createdOrder.getId() > 0);
        assertEquals(OrderStatus.NEW, createdOrder.getStatus());
    }
}