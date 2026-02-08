package com.satishlabs.order.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.satishlabs.order.entity.Order;
import com.satishlabs.order.entity.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Order> findByStatus(OrderStatus status);
    Optional<Order> findByOrderNumber(String orderNumber);
}
