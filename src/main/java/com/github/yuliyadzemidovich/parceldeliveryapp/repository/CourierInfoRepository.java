package com.github.yuliyadzemidovich.parceldeliveryapp.repository;

import com.github.yuliyadzemidovich.parceldeliveryapp.entity.CourierInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourierInfoRepository extends JpaRepository<CourierInfo, Long> {
}
