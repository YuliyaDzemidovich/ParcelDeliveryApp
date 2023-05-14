package com.github.yuliyadzemidovich.parceldeliveryapp.repository;

import com.github.yuliyadzemidovich.parceldeliveryapp.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllBySenderId(long senderId);
}
