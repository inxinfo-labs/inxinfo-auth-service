package com.satishlabs.order.service;

import java.util.List;

import com.satishlabs.order.dto.request.CreateOrderRequest;
import com.satishlabs.order.dto.response.OrderResponse;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request, Long userId);
    List<OrderResponse> getUserOrders(Long userId);
    OrderResponse getOrderById(Long orderId, Long userId);
    OrderResponse updateOrderStatus(Long orderId, String status);
    OrderResponse getOrderByOrderNumber(String orderNumber, Long userId);
    OrderResponse confirmPayment(Long orderId, String paymentId, Long userId);
}
