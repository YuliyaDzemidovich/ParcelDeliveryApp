package com.github.yuliyadzemidovich.parceldeliveryapp.service.impl;

import com.github.yuliyadzemidovich.parceldeliveryapp.dto.DeliveryDto;
import com.github.yuliyadzemidovich.parceldeliveryapp.dto.OrderDto;
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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import static com.github.yuliyadzemidovich.parceldeliveryapp.entity.Role.ROLE_SUPER_ADMIN;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    static final String MISMATCH_SENDER_ID_AND_AUTH_USER_ID = "Sender ID must match authenticated user ID";

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
            delivery.setPickupTime(Timestamp.from(Instant.now()));
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

    private boolean isUserSuperAdmin(Authentication auth) {
        Optional<? extends GrantedAuthority> superAdminAuthority = auth.getAuthorities().stream()
                .filter(ga -> ROLE_SUPER_ADMIN.getSecurityValue().equals(ga.getAuthority())).findFirst();
        return superAdminAuthority.isPresent();
    }

    private OrderDto map(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .receiverPhone(order.getReceiverPhone())
                .receiverAddress(order.getReceiverAddress())
                .receiverName(order.getReceiverName())
                .senderId(order.getSender().getId())
                .status(order.getStatus())
                .build();
    }
}
