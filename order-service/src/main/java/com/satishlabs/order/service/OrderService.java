package com.satishlabs.order.service;

import java.util.List;

import com.satishlabs.order.dto.request.CreateOrderRequest;
import com.satishlabs.order.dto.response.OrderResponse;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request, Long userId, String authorizationHeader);
    List<OrderResponse> getUserOrders(Long userId, String authorizationHeader);
    List<OrderResponse> getAllOrdersForAdmin(String authorizationHeader);
    OrderResponse getOrderById(Long orderId, Long userId, String authorizationHeader);
    OrderResponse updateOrderStatus(Long orderId, String status);
    OrderResponse getOrderByOrderNumber(String orderNumber, Long userId, String authorizationHeader);
    OrderResponse confirmPayment(Long orderId, String paymentId, Long userId, String authorizationHeader);
}
