package com.github.yuliyadzemidovich.parceldeliveryapp.repository;

import com.github.yuliyadzemidovich.parceldeliveryapp.entity.CourierLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourierLocationRepository extends JpaRepository<CourierLocation, Long> {
}
