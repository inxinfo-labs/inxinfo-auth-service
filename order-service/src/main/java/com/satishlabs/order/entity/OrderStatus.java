package com.satishlabs.order.entity;

public enum OrderStatus {
    PENDING,
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    OUT_FOR_DELIVERY,
    DELIVERED,
    PARTIALLY_DELIVERED,
    CANCELLED,
    RETURNED,
    REFUNDED,
    FAILED
}
