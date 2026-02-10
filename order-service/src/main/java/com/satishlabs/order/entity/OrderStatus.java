package com.satishlabs.order.entity;

import java.util.Locale;

public enum OrderStatus {
    PENDING("Pending (payment pending)"),
    CONFIRMED("Confirmed"),
    PROCESSING("Processing"),
    SHIPPED("Shipped"),
    OUT_FOR_DELIVERY("Out for delivery"),
    DELIVERED("Delivered"),
    PARTIALLY_DELIVERED("Partially delivered"),
    CANCELLED("Cancelled"),
    RETURNED("Returned"),
    REFUNDED("Refunded"),
    FAILED("Failed");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /** Lowercase key for API/frontend (e.g. "pending", "out_for_delivery"). */
    public String getKey() {
        return name().toLowerCase(Locale.ROOT);
    }
}
