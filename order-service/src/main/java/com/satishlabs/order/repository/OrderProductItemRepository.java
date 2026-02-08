package com.satishlabs.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.satishlabs.order.entity.OrderProductItem;

@Repository
public interface OrderProductItemRepository extends JpaRepository<OrderProductItem, Long> {
}
