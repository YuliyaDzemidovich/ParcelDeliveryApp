package com.github.yuliyadzemidovich.parceldeliveryapp.service.impl;

import com.github.yuliyadzemidovich.parceldeliveryapp.ParcelDeliveryAppApplication;
import com.github.yuliyadzemidovich.parceldeliveryapp.TestUtil;
import com.github.yuliyadzemidovich.parceldeliveryapp.config.SecurityConfig;
import com.github.yuliyadzemidovich.parceldeliveryapp.dto.DeliveryDto;
import com.github.yuliyadzemidovich.parceldeliveryapp.dto.OrderDto;
import com.github.yuliyadzemidovich.parceldeliveryapp.dto.ParcelDto;
import com.github.yuliyadzemidovich.parceldeliveryapp.entity.Order;
import com.github.yuliyadzemidovich.parceldeliveryapp.entity.OrderStatus;
import com.github.yuliyadzemidovich.parceldeliveryapp.entity.User;
import com.github.yuliyadzemidovich.parceldeliveryapp.exception.ValidationException;
import com.github.yuliyadzemidovich.parceldeliveryapp.exception.WebException;
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
import java.util.List;

import static com.github.yuliyadzemidovich.parceldeliveryapp.TestUtil.TEST_SUPER_ADMIN_EMAIL;
import static com.github.yuliyadzemidovich.parceldeliveryapp.TestUtil.TEST_SUPER_ADMIN_PWD;
import static com.github.yuliyadzemidovich.parceldeliveryapp.TestUtil.TEST_USER_1_EMAIL;
import static com.github.yuliyadzemidovich.parceldeliveryapp.TestUtil.TEST_USER_1_PWD;
import static com.github.yuliyadzemidovich.parceldeliveryapp.service.impl.OrderServiceImpl.MISMATCH_SENDER_ID_AND_AUTH_USER_ID;
import static com.github.yuliyadzemidovich.parceldeliveryapp.service.impl.OrderServiceImpl.ORDER_CANT_BE_CANCELED;
import static com.github.yuliyadzemidovich.parceldeliveryapp.service.impl.OrderServiceImpl.ORDER_NOT_FOUND;
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

    @Test
    @WithMockUser(username = TEST_USER_1_EMAIL, password = TEST_USER_1_PWD, roles = "USER")
    void getUserOrders() {
        // pre create order
        User sender = TestUtil.getUser1();
        sender = userRepo.findByEmail(sender.getEmail());
        long senderId = sender.getId();
        OrderDto orderReq = getOrderReq(senderId);
        OrderDto createdOrder = orderService.createOrder(orderReq);

        // method under test
        List<OrderDto> fetchedOrders = orderService.getUserOrders();

        // assertions
        assertNotNull(fetchedOrders);
        assertEquals(1, fetchedOrders.size());
        OrderDto fetchedOrder = fetchedOrders.get(0);
        assertEquals(createdOrder.getId(), fetchedOrder.getId());
        assertEquals(createdOrder.getSenderId(), fetchedOrder.getSenderId());
        assertEquals(createdOrder.getStatus(), fetchedOrder.getStatus());
        assertEquals(createdOrder.getReceiverName(), fetchedOrder.getReceiverName());
        assertEquals(createdOrder.getReceiverAddress(), fetchedOrder.getReceiverAddress());
        assertEquals(createdOrder.getReceiverPhone(), fetchedOrder.getReceiverPhone());

        // ParcelDto assertions
        assertEquals(createdOrder.getParcelDto().getId(), fetchedOrder.getParcelDto().getId());
        assertEquals(0, createdOrder.getParcelDto().getWeight().compareTo(fetchedOrder.getParcelDto().getWeight()));

        // DeliveryDto assertions
        DeliveryDto createdDeliveryDto = createdOrder.getDeliveryDto();
        DeliveryDto fetchedDeliveryDto = fetchedOrder.getDeliveryDto();
        assertEquals(createdDeliveryDto.getId(), fetchedDeliveryDto.getId());
        double precision = 0.01;
        assertTrue(TestUtil.areBigDecimalsEqual(
                createdDeliveryDto.getPickupLatitude(),
                fetchedDeliveryDto.getPickupLatitude(),
                precision));
        assertTrue(TestUtil.areBigDecimalsEqual(
                createdDeliveryDto.getPickupLongitude(),
                fetchedDeliveryDto.getPickupLongitude(),
                precision));
        assertEquals(createdDeliveryDto.getPickupTime(), fetchedDeliveryDto.getPickupTime());
    }

    @Test
    @WithMockUser(username = TEST_USER_1_EMAIL, password = TEST_USER_1_PWD, roles = "USER")
    void getUserOrdersEmptyList() {
        List<OrderDto> fetchedOrders = orderService.getUserOrders();
        assertNotNull(fetchedOrders);
        assertTrue(fetchedOrders.isEmpty());
    }

    @Test
    @WithMockUser(username = TEST_USER_1_EMAIL, password = TEST_USER_1_PWD, roles = "USER")
    void cancelOrder() {
        User sender = TestUtil.getUser1();
        sender = userRepo.findByEmail(sender.getEmail());
        long senderId = sender.getId();

        // pre create order
        OrderDto orderReq = getOrderReq(senderId);
        OrderDto createdOrder = orderService.createOrder(orderReq);

        // method under test
        OrderDto canceledOrder = orderService.cancelOrder(createdOrder.getId());
        assertNotNull(canceledOrder);
        assertEquals(createdOrder.getId(), canceledOrder.getId());
        assertEquals(OrderStatus.CANCELLED, canceledOrder.getStatus());
    }

    @Test
    @WithMockUser(username = TEST_USER_1_EMAIL, password = TEST_USER_1_PWD, roles = "USER")
    void cancelOrderTooLate() {
        User sender = TestUtil.getUser1();
        sender = userRepo.findByEmail(sender.getEmail());
        long senderId = sender.getId();

        // pre create order
        OrderDto orderReq = getOrderReq(senderId);
        OrderDto createdOrder = orderService.createOrder(orderReq);

        // change order status to RUNNING
        Long orderId = createdOrder.getId();
        Order existingOrder = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Can't find pre created order by ID"));
        existingOrder.setStatus(OrderStatus.RUNNING);
        orderRepo.save(existingOrder);

        // method under test
        WebException ex = assertThrows(
                WebException.class,
                () -> orderService.cancelOrder(orderId)
        );
        assertTrue(ex.getMessage().contentEquals(String.format(ORDER_CANT_BE_CANCELED, orderId)));
    }

    /**
     * When order belongs to user 2
     * and user 1 tries to cancel the order,
     * then throw order not found exception.
     */
    @Test
    @WithMockUser(username = TEST_USER_1_EMAIL, password = TEST_USER_1_PWD, roles = "USER")
    void cancelOrderOfAnotherUser() {
        User sender = TestUtil.getUser1();
        sender = userRepo.findByEmail(sender.getEmail());
        long senderId = sender.getId();

        // pre create order
        OrderDto orderReq = getOrderReq(senderId);
        OrderDto createdOrder = orderService.createOrder(orderReq);

        // change order status to RUNNING
        Long orderId = createdOrder.getId();
        Order existingOrder = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Can't find pre created order by ID"));
        existingOrder.setStatus(OrderStatus.RUNNING);

        // change order sender (emulate like it was initially created by user 2)
        sender = TestUtil.getUser2();
        sender = userRepo.findByEmail(sender.getEmail());
        existingOrder.setSender(sender);
        orderRepo.save(existingOrder);

        // method under test - authed user 1 tries to cancel order of user 2
        WebException ex = assertThrows(
                WebException.class,
                () -> orderService.cancelOrder(orderId)
        );
        assertTrue(ex.getMessage().contentEquals(String.format(ORDER_NOT_FOUND, orderId)));
    }

    @Test
    @WithMockUser(username = TEST_USER_1_EMAIL, password = TEST_USER_1_PWD, roles = "USER")
    void cancelAllOrders() {
        User sender = TestUtil.getUser1();
        sender = userRepo.findByEmail(sender.getEmail());
        long senderId = sender.getId();

        // pre create orders
        OrderDto orderReq = getOrderReq(senderId);
        orderService.createOrder(orderReq); // order1
        orderService.createOrder(orderReq); // order2

        // method under test
        orderService.cancelAllOrders();
        List<Order> updatedOrders = orderRepo.findAllBySenderId(senderId);
        assertNotNull(updatedOrders);
        assertEquals(2, updatedOrders.size());
        assertAll(() -> {
            for (Order order : updatedOrders) {
                assertEquals(OrderStatus.CANCELLED, order.getStatus());
            }
        });
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
