package com.github.yuliyadzemidovich.parceldeliveryapp.service.impl;

import com.github.yuliyadzemidovich.parceldeliveryapp.ParcelDeliveryAppApplication;
import com.github.yuliyadzemidovich.parceldeliveryapp.TestUtil;
import com.github.yuliyadzemidovich.parceldeliveryapp.config.SecurityConfig;
import com.github.yuliyadzemidovich.parceldeliveryapp.dto.DeliveryDto;
import com.github.yuliyadzemidovich.parceldeliveryapp.dto.OrderDto;
import com.github.yuliyadzemidovich.parceldeliveryapp.dto.ParcelDto;
import com.github.yuliyadzemidovich.parceldeliveryapp.entity.OrderStatus;
import com.github.yuliyadzemidovich.parceldeliveryapp.entity.User;
import com.github.yuliyadzemidovich.parceldeliveryapp.exception.ValidationException;
import com.github.yuliyadzemidovich.parceldeliveryapp.repository.OrderRepository;
import com.github.yuliyadzemidovich.parceldeliveryapp.repository.UserRepository;
import com.github.yuliyadzemidovich.parceldeliveryapp.service.OrderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.Arrays;

import static com.github.yuliyadzemidovich.parceldeliveryapp.TestUtil.TEST_SUPER_ADMIN_EMAIL;
import static com.github.yuliyadzemidovich.parceldeliveryapp.TestUtil.TEST_SUPER_ADMIN_PWD;
import static com.github.yuliyadzemidovich.parceldeliveryapp.TestUtil.TEST_USER_1_EMAIL;
import static com.github.yuliyadzemidovich.parceldeliveryapp.TestUtil.TEST_USER_1_PWD;
import static com.github.yuliyadzemidovich.parceldeliveryapp.service.impl.OrderServiceImpl.MISMATCH_SENDER_ID_AND_AUTH_USER_ID;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for Service layer. Uses H2 in-memory database, Spring container - spring beans, JPA repositories.
 */
@SpringBootTest(classes = {
        SecurityConfig.class,
        ParcelDeliveryAppApplication.class})
@AutoConfigureTestDatabase // use in-memory H2
class OrderServiceIntegrationTest {

    @Autowired
    UserRepository userRepo;
    @Autowired
    OrderRepository orderRepo;

    @Autowired
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        // create users
        User user1 = TestUtil.getUser1();
        User user2 = TestUtil.getUser2();
        User saUser = TestUtil.getSuperAdmin();
        userRepo.saveAll(Arrays.asList(user1, user2, saUser));
    }

    @Test
    @WithMockUser(username = TEST_USER_1_EMAIL, password = TEST_USER_1_PWD, roles = "USER")
    void createOrder() {
        User sender = TestUtil.getUser1();
        sender = userRepo.findByEmail(sender.getEmail());
        long senderId = sender.getId();

        // prepare new order request
        OrderDto orderReq = getOrderReq(senderId);

        // method under test
        OrderDto createdOrder = orderService.createOrder(orderReq);
        assertNotNull(createdOrder);
        assertTrue(createdOrder.getId() > 0);
        assertEquals(OrderStatus.NEW, createdOrder.getStatus());
        assertEquals(senderId, createdOrder.getSenderId());
    }

    @Test
    @WithMockUser(username = TEST_USER_1_EMAIL, password = TEST_USER_1_PWD, roles = "USER")
    void createOrderWithDifferentUserNotAllowedForUserRole() {
        User anotherUser = TestUtil.getUser2();
        anotherUser = userRepo.findByEmail(anotherUser.getEmail());
        long anotherUserId = anotherUser.getId();

        // prepare new order request
        OrderDto orderReq = getOrderReq(anotherUserId);

        // method under test
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> orderService.createOrder(orderReq)
        );
        assertTrue(ex.getMessage().contentEquals(MISMATCH_SENDER_ID_AND_AUTH_USER_ID));
    }

    @Test
    @WithMockUser(username = TEST_SUPER_ADMIN_EMAIL, password = TEST_SUPER_ADMIN_PWD, roles = "SUPER_ADMIN")
    void createOrderWithDifferentUserAllowedForSuperAdminRole() {
        User anotherUser = TestUtil.getUser2();
        anotherUser = userRepo.findByEmail(anotherUser.getEmail());
        long anotherUserId = anotherUser.getId();

        // prepare new order request
        OrderDto orderReq = getOrderReq(anotherUserId);

        // method under test
        OrderDto createdOrder = orderService.createOrder(orderReq);
        assertNotNull(createdOrder);
        assertTrue(createdOrder.getId() > 0);
        assertEquals(OrderStatus.NEW, createdOrder.getStatus());
        assertEquals(anotherUserId, createdOrder.getSenderId());
    }

    private OrderDto getOrderReq(long SENDER_ID) {
        ParcelDto parcelDto = ParcelDto.builder()
                .weight(new BigDecimal(5))
                .build();
        DeliveryDto deliveryDto = DeliveryDto.builder()
                .pickupLatitude("43.69364466860889")
                .pickupLongitude("32.05835110656418")
                .build();
        return OrderDto.builder()
                .receiverName("Alice")
                .receiverAddress("505 North Street")
                .receiverPhone("555-789-041")
                .parcelDto(parcelDto)
                .deliveryDto(deliveryDto)
                .senderId(SENDER_ID)
                .build();
    }

    @AfterEach
    void cleanUp() {
        orderRepo.deleteAll();
        userRepo.deleteAll();
    }
}
