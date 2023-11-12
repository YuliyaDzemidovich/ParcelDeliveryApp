package com.github.yuliyadzemidovich.parceldeliveryapp.service;

import com.github.yuliyadzemidovich.parceldeliveryapp.dto.CourierDto;

import java.util.List;

/**
 * Service to manage Couriers.
 */
public interface CourierService {

    /**
     * Get all couriers from the database
     * @return list of couriers DTOs
     */
    List<CourierDto> getAllCouriers();
}
