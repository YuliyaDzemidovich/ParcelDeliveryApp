package com.github.yuliyadzemidovich.parceldeliveryapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "deliveries")
@Data
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parcel_id")
    private Parcel parcel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courier_id")
    private CourierInfo courierInfo;

    @NotNull
    private BigDecimal pickupLatitude;

    @NotNull
    private BigDecimal pickupLongitude;

    @NotNull
    private BigDecimal deliveryLatitude;

    @NotNull
    private BigDecimal deliveryLongitude;

    @NotNull
    private java.sql.Timestamp pickupTime;

    private java.sql.Timestamp deliveryTime;

    @NotBlank
    @Size(max = 20)
    private String status;
}
