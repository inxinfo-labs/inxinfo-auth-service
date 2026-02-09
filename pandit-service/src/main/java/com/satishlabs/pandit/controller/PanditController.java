package com.satishlabs.pandit.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.satishlabs.auth.dto.response.ApiResponse;
import com.satishlabs.auth.entity.User;
import com.satishlabs.auth.exception.ResourceNotFoundException;
import com.satishlabs.auth.repository.UserRepository;
import com.satishlabs.auth.security.XUserIdPrincipal;
import com.satishlabs.pandit.dto.request.PanditBookingRequest;
import com.satishlabs.pandit.dto.request.ReleaseRequest;
import com.satishlabs.pandit.dto.request.ReserveRequest;
import com.satishlabs.pandit.dto.response.AvailabilityResponse;
import com.satishlabs.pandit.dto.response.PanditBookingResponse;
import com.satishlabs.pandit.dto.response.PanditResponse;
import com.satishlabs.pandit.service.PanditService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/pandit")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class PanditController {

    private final PanditService panditService;
    private final UserRepository userRepository;

    private Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ResourceNotFoundException("User not authenticated");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof XUserIdPrincipal) {
            return ((XUserIdPrincipal) principal).getUserId();
        }
        if (!(principal instanceof UserDetails)) {
            throw new ResourceNotFoundException("Invalid authentication");
        }
        User user = userRepository.findByEmail(((UserDetails) principal).getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user.getId();
    }

    private static String authHeader(HttpServletRequest request) {
        String h = request.getHeader("Authorization");
        return h != null ? h : "";
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PanditResponse>>> getAllPandits() {
        return ResponseEntity.ok(
                new ApiResponse<>(4001, "Pandits fetched successfully", panditService.getAllPandits())
        );
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<PanditResponse>>> getAvailablePandits() {
        return ResponseEntity.ok(
                new ApiResponse<>(4002, "Available pandits fetched successfully", 
                        panditService.getAvailablePandits())
        );
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<ApiResponse<List<PanditResponse>>> getPanditsByCity(@PathVariable String city) {
        return ResponseEntity.ok(
                new ApiResponse<>(4003, "Pandits fetched successfully", 
                        panditService.getPanditsByCity(city))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PanditResponse>> getPanditById(@PathVariable Long id) {
        return ResponseEntity.ok(
                new ApiResponse<>(4004, "Pandit fetched successfully", panditService.getPanditById(id))
        );
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<ApiResponse<AvailabilityResponse>> checkAvailability(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(
                new ApiResponse<>(4005, "Availability checked successfully", 
                        panditService.checkAvailability(id, date))
        );
    }

    @PostMapping("/book")
    public ResponseEntity<ApiResponse<PanditBookingResponse>> bookPandit(
            @Valid @RequestBody PanditBookingRequest request,
            Authentication authentication,
            HttpServletRequest requestHttp) {
        Long userId = getUserIdFromAuthentication(authentication);
        return ResponseEntity.ok(
                new ApiResponse<>(4006, "Pandit booked successfully",
                        panditService.bookPandit(request, userId, authHeader(requestHttp)))
        );
    }

    @GetMapping("/bookings")
    public ResponseEntity<ApiResponse<List<PanditBookingResponse>>> getUserBookings(
            Authentication authentication,
            HttpServletRequest request) {
        Long userId = getUserIdFromAuthentication(authentication);
        return ResponseEntity.ok(
                new ApiResponse<>(4007, "Bookings fetched successfully",
                        panditService.getUserBookings(userId, authHeader(request)))
        );
    }

    @GetMapping("/bookings/{id}")
    public ResponseEntity<ApiResponse<PanditBookingResponse>> getBookingById(
            @PathVariable Long id,
            Authentication authentication,
            HttpServletRequest request) {
        Long userId = getUserIdFromAuthentication(authentication);
        return ResponseEntity.ok(
                new ApiResponse<>(4008, "Booking fetched successfully",
                        panditService.getBookingById(id, userId, authHeader(request)))
        );
    }

    @PostMapping("/bookings/{id}/payment/confirm")
    public ResponseEntity<ApiResponse<PanditBookingResponse>> confirmBookingPayment(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        return ResponseEntity.ok(
                new ApiResponse<>(4010, "Booking payment confirmed",
                        panditService.confirmBookingPayment(id, userId))
        );
    }

    /** Saga: reserve pandit for order (called by order-service) */
    @PostMapping("/reserve")
    public ResponseEntity<ApiResponse<Long>> reserve(@Valid @RequestBody ReserveRequest request) {
        Long reservationId = panditService.reserve(request);
        return ResponseEntity.ok(new ApiResponse<>(4009, "Pandit reserved", reservationId));
    }

    /** Saga: release reservation (compensation) */
    @PostMapping("/release")
    public ResponseEntity<ApiResponse<Void>> release(@Valid @RequestBody ReleaseRequest request) {
        panditService.release(request);
        return ResponseEntity.ok(new ApiResponse<>(4010, "Reservation released", null));
    }
}
