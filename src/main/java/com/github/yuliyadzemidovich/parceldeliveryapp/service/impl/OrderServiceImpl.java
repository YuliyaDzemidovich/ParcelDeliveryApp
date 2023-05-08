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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepo;
    private final UserRepository userRepo;

    @Override
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

        // set sender user // todo add check user creates on his behalf, admin can create for anyone
        Long senderId = orderDto.getSenderId();
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

    private OrderDto map(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .receiverPhone(order.getReceiverPhone())
                .receiverAddress(order.getReceiverAddress())
                .receiverName(order.getReceiverName())
                .status(order.getStatus())
                .build();
    }
}
