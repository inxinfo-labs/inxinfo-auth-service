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
import com.satishlabs.pandit.dto.request.PanditBookingRequest;
import com.satishlabs.pandit.dto.response.AvailabilityResponse;
import com.satishlabs.pandit.dto.response.PanditBookingResponse;
import com.satishlabs.pandit.dto.response.PanditResponse;
import com.satishlabs.pandit.service.PanditService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pandit")
@RequiredArgsConstructor
public class PanditController {

    private final PanditService panditService;
    private final UserRepository userRepository;

    private Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ResourceNotFoundException("User not authenticated");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user.getId();
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
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        return ResponseEntity.ok(
                new ApiResponse<>(4006, "Pandit booked successfully", 
                        panditService.bookPandit(request, userId))
        );
    }

    @GetMapping("/bookings")
    public ResponseEntity<ApiResponse<List<PanditBookingResponse>>> getUserBookings(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        return ResponseEntity.ok(
                new ApiResponse<>(4007, "Bookings fetched successfully", 
                        panditService.getUserBookings(userId))
        );
    }

    @GetMapping("/bookings/{id}")
    public ResponseEntity<ApiResponse<PanditBookingResponse>> getBookingById(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        return ResponseEntity.ok(
                new ApiResponse<>(4008, "Booking fetched successfully", 
                        panditService.getBookingById(id, userId))
        );
    }
}
