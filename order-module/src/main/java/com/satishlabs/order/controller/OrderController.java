package com.satishlabs.order.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.satishlabs.auth.dto.response.ApiResponse;
import com.satishlabs.auth.entity.User;
import com.satishlabs.auth.exception.ResourceNotFoundException;
import com.satishlabs.auth.exception.UnauthorizedException;
import com.satishlabs.auth.repository.UserRepository;
import com.satishlabs.order.dto.request.CreateOrderRequest;
import com.satishlabs.order.dto.response.OrderResponse;
import com.satishlabs.order.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:3000/")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    private Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails)) {
            throw new UnauthorizedException("Invalid authentication");
        }
        UserDetails userDetails = (UserDetails) principal;
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        return user.getId();
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        return ResponseEntity.ok(
                new ApiResponse<>(3001, "Order created successfully", orderService.createOrder(request, userId))
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getUserOrders(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        return ResponseEntity.ok(
                new ApiResponse<>(3002, "Orders fetched successfully", orderService.getUserOrders(userId))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        return ResponseEntity.ok(
                new ApiResponse<>(3003, "Order fetched successfully", 
                        orderService.getOrderById(id, userId))
        );
    }

    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderByOrderNumber(
            @PathVariable String orderNumber,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        return ResponseEntity.ok(
                new ApiResponse<>(3004, "Order fetched successfully", 
                        orderService.getOrderByOrderNumber(orderNumber, userId))
        );
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(
                new ApiResponse<>(3005, "Order status updated successfully", 
                        orderService.updateOrderStatus(id, status))
        );
    }

    @PostMapping("/{id}/payment/confirm")
    public ResponseEntity<ApiResponse<OrderResponse>> confirmPayment(
            @PathVariable Long id,
            @RequestBody(required = false) java.util.Map<String, String> body,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        String paymentId = body != null ? body.get("paymentId") : null;
        return ResponseEntity.ok(
                new ApiResponse<>(3006, "Payment confirmed", orderService.confirmPayment(id, paymentId, userId))
        );
    }
}
