package com.github.yuliyadzemidovich.parceldeliveryapp.repository;

import com.github.yuliyadzemidovich.parceldeliveryapp.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
