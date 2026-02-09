package com.satishlabs.order.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.satishlabs.auth.dto.response.ApiResponse;
import com.satishlabs.order.dto.response.OrderResponse;
import com.satishlabs.order.service.OrderService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    private static String authHeader(HttpServletRequest request) {
        String h = request.getHeader("Authorization");
        return h != null ? h : "";
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders(HttpServletRequest request) {
        List<OrderResponse> orders = orderService.getAllOrdersForAdmin(authHeader(request));
        return ResponseEntity.ok(new ApiResponse<>(5020, "Orders fetched successfully", orders));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {
        String orderStatus = body != null ? body.get("orderStatus") : null;
        if (orderStatus == null || orderStatus.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        OrderResponse updated = orderService.updateOrderStatus(id, orderStatus);
        return ResponseEntity.ok(new ApiResponse<>(5021, "Order status updated", updated));
    }
}
