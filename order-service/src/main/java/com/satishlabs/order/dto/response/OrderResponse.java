package com.satishlabs.order.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.satishlabs.order.entity.OrderStatus;
import com.satishlabs.order.entity.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private Long userId;
    private String userName;
    private List<OrderItemResponse> orderItems;
    private List<ProductItemResponse> productItems;
    private BigDecimal totalAmount;
    private OrderStatus status;
    /** Human-readable label (e.g. "Pending (payment pending)", "Out for delivery"). */
    private String statusDisplayName;
    /** Lowercase key for frontend (e.g. "pending", "out_for_delivery"). */
    private String statusKey;
    private PaymentStatus paymentStatus;
    private String paymentId;
    private String shippingAddress;
    private String city;
    private String state;
    private String pincode;
    private String contactPhone;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
