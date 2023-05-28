package com.github.yuliyadzemidovich.parceldeliveryapp.service.impl;

import com.github.yuliyadzemidovich.parceldeliveryapp.dto.DeliveryDto;
import com.github.yuliyadzemidovich.parceldeliveryapp.dto.OrderDto;
import com.github.yuliyadzemidovich.parceldeliveryapp.dto.ParcelDto;
import com.github.yuliyadzemidovich.parceldeliveryapp.entity.Delivery;
import com.github.yuliyadzemidovich.parceldeliveryapp.entity.Order;
import com.github.yuliyadzemidovich.parceldeliveryapp.entity.OrderStatus;
import com.github.yuliyadzemidovich.parceldeliveryapp.entity.Parcel;
import com.github.yuliyadzemidovich.parceldeliveryapp.entity.User;
import com.github.yuliyadzemidovich.parceldeliveryapp.exception.ValidationException;
import com.github.yuliyadzemidovich.parceldeliveryapp.exception.WebException;
import com.github.yuliyadzemidovich.parceldeliveryapp.repository.OrderRepository;
import com.github.yuliyadzemidovich.parceldeliveryapp.repository.UserRepository;
import com.github.yuliyadzemidovich.parceldeliveryapp.service.OrderService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.github.yuliyadzemidovich.parceldeliveryapp.entity.Role.ROLE_SUPER_ADMIN;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    static final String MISMATCH_SENDER_ID_AND_AUTH_USER_ID = "Sender ID must match authenticated user ID";
    static final String ORDER_NOT_FOUND = "Order with ID = %s not found";
    static final String ORDER_CANT_BE_CANCELED = "Order with ID = %s cannot be canceled at this stage";

    private final OrderRepository orderRepo;
    private final UserRepository userRepo;

    @Override
    @Transactional
    public OrderDto createOrder(OrderDto orderDto) {
        Order order = new Order();
        order.setStatus(OrderStatus.NEW);

        // create Parcel entity
        Parcel parcel = new Parcel();
        parcel.setWeight(orderDto.getParcelDto().getWeight());
        order.setParcel(parcel);

        // create Delivery entity
        Delivery delivery = new Delivery();
        DeliveryDto deliveryDto = orderDto.getDeliveryDto();
        String pickupTime = deliveryDto.getPickupTime();
        if (StringUtils.isEmpty(pickupTime)) {
            delivery.setPickupTime(Timestamp.from(Instant.now().truncatedTo(ChronoUnit.SECONDS)));
        } else {
            delivery.setPickupTime(Timestamp.valueOf(pickupTime));
        }
        try {
            delivery.setPickupLatitude(new BigDecimal(deliveryDto.getPickupLatitude()));
            delivery.setPickupLongitude(new BigDecimal(deliveryDto.getPickupLongitude()));
        } catch (NumberFormatException e) {
            throw new ValidationException(
                    "Unrecognized delivery coordinates. Please use format, for example \"43.69364466860889\" ");
        }
        order.setDelivery(delivery);

        // validate that user creates order on his behalf, admin can create for anyone
        Long senderId = orderDto.getSenderId();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!isUserSuperAdmin(auth)) {
            String authedUserEmail = auth.getName();
            long authedUserId = userRepo.findByEmail(authedUserEmail).getId();
            if (authedUserId != senderId) {
                throw new ValidationException(MISMATCH_SENDER_ID_AND_AUTH_USER_ID, HttpStatus.BAD_REQUEST);
            }
        }
        User sender = userRepo.findById(senderId)
                .orElseThrow(() -> new WebException(String.format(
                        "Sender user with id=%d not found", senderId), HttpStatus.BAD_REQUEST));
        order.setSender(sender);

        // map other Order fields
        order.setReceiverName(orderDto.getReceiverName());
        order.setReceiverAddress(orderDto.getReceiverAddress());
        order.setReceiverPhone(orderDto.getReceiverPhone());
        Order savedOrder = orderRepo.save(order);
        return map(savedOrder);
    }

    @Override
    public List<OrderDto> getUserOrders() {
        long authedUserId = getAuthedUserId();
        List<Order> orders = orderRepo.findAllBySenderId(authedUserId);
        if (CollectionUtils.isEmpty(orders)) {
            return new ArrayList<>();
        }
        return orders.stream().map(this::map).toList();
    }

    private long getAuthedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String authedUserEmail = auth.getName();
        return userRepo.findByEmail(authedUserEmail).getId();
    }

    @Override
    public OrderDto cancelOrder(long orderId) {
        long authedUserId = getAuthedUserId();
        Optional<Order> orderOpt = orderRepo.findById(orderId);
        if (orderOpt.isEmpty() || authedUserId != orderOpt.get().getSender().getId()) {
            throw new WebException(String.format(ORDER_NOT_FOUND, orderId), HttpStatus.NOT_FOUND);
        }
        Order order = orderOpt.get();
        if (!canBeCanceled(order.getStatus())) {
            throw new WebException(String.format(ORDER_CANT_BE_CANCELED, orderId), HttpStatus.BAD_REQUEST);
        }
        order.setStatus(OrderStatus.CANCELLED);
        order = orderRepo.save(order);
        return map(order);
    }

    @Override
    public boolean canBeCanceled(OrderStatus order) {
        return order == OrderStatus.NEW || order == OrderStatus.CANCELLED;
    }

    @Override
    public OrderDto getOrderById(long orderId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Order> orderOpt = orderRepo.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new WebException(String.format(ORDER_NOT_FOUND, orderId), HttpStatus.NOT_FOUND);
        }
        Order order = orderOpt.get();
        if (!isUserSuperAdmin(auth)) {
            long authedUserId = getAuthedUserId();
            if (authedUserId != order.getSender().getId()) {
                throw new WebException(String.format(ORDER_NOT_FOUND, orderId), HttpStatus.NOT_FOUND);
            }
        }
        return map(order);
    }

    private boolean isUserSuperAdmin(Authentication auth) {
        Optional<? extends GrantedAuthority> superAdminAuthority = auth.getAuthorities().stream()
                .filter(ga -> ROLE_SUPER_ADMIN.getSecurityValue().equals(ga.getAuthority())).findFirst();
        return superAdminAuthority.isPresent();
    }

    private OrderDto map(Order order) {
        Parcel parcel = order.getParcel();
        ParcelDto parcelDto = ParcelDto.builder()
                .id(parcel.getId())
                .weight(parcel.getWeight())
                .build();
        Delivery delivery = order.getDelivery();
        DeliveryDto deliveryDto = DeliveryDto.builder()
                .id(delivery.getId())
                .build();
        if (delivery.getPickupTime() != null) {
            deliveryDto.setPickupTime(delivery.getPickupTime().toString());
        }
        if (delivery.getPickupLatitude() != null) {
            deliveryDto.setPickupLatitude(delivery.getPickupLatitude().toString());
        }
        if (delivery.getPickupLongitude() != null) {
            deliveryDto.setPickupLongitude(delivery.getPickupLongitude().toString());
        }
        return OrderDto.builder()
                .id(order.getId())
                .receiverPhone(order.getReceiverPhone())
                .receiverAddress(order.getReceiverAddress())
                .receiverName(order.getReceiverName())
                .senderId(order.getSender().getId())
                .status(order.getStatus())
                .parcelDto(parcelDto)
                .deliveryDto(deliveryDto)
                .build();
    }
}
