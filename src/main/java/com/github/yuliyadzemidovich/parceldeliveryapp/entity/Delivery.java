package com.github.yuliyadzemidovich.parceldeliveryapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "deliveries")
@Data
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courier_id")
    private CourierInfo courierInfo;

    @NotNull
    private BigDecimal pickupLatitude;

    @NotNull
    private BigDecimal pickupLongitude;

    private BigDecimal deliveryLatitude;

    private BigDecimal deliveryLongitude;

    @NotNull
    private java.sql.Timestamp pickupTime;

    private java.sql.Timestamp deliveryTime;
}
