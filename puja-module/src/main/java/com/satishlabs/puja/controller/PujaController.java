package com.satishlabs.puja.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.satishlabs.auth.dto.response.ApiResponse;
import com.satishlabs.auth.entity.User;
import com.satishlabs.auth.exception.ResourceNotFoundException;
import com.satishlabs.auth.repository.UserRepository;
import com.satishlabs.puja.dto.request.PujaBookingRequest;
import com.satishlabs.puja.dto.response.PujaBookingResponse;
import com.satishlabs.puja.dto.response.PujaTypeResponse;
import com.satishlabs.puja.entity.PujaCategory;
import com.satishlabs.puja.service.PujaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/puja")
@CrossOrigin(origins = "http://localhost:3000/")
@RequiredArgsConstructor
public class PujaController {

    private final PujaService pujaService;
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
    public ResponseEntity<ApiResponse<List<PujaTypeResponse>>> getAllPujaTypes() {
        return ResponseEntity.ok(
                new ApiResponse<>(2001, "Puja types fetched successfully", pujaService.getAllPujaTypes())
        );
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<PujaTypeResponse>>> getPujaTypesByCategory(
            @PathVariable String category) {
        try {
            PujaCategory pujaCategory = PujaCategory.valueOf(category.toUpperCase());
            return ResponseEntity.ok(
                    new ApiResponse<>(2002, "Puja types fetched successfully", 
                            pujaService.getPujaTypesByCategory(pujaCategory))
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PujaTypeResponse>> getPujaTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(
                new ApiResponse<>(2003, "Puja type fetched successfully", pujaService.getPujaTypeById(id))
        );
    }

    @PostMapping("/book")
    public ResponseEntity<ApiResponse<PujaBookingResponse>> bookPuja(
            @Valid @RequestBody PujaBookingRequest request,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        return ResponseEntity.ok(
                new ApiResponse<>(2004, "Puja booked successfully", pujaService.bookPuja(request, userId))
        );
    }

    @GetMapping("/bookings")
    public ResponseEntity<ApiResponse<List<PujaBookingResponse>>> getUserBookings(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        return ResponseEntity.ok(
                new ApiResponse<>(2005, "Bookings fetched successfully", pujaService.getUserBookings(userId))
        );
    }

    @GetMapping("/bookings/{id}")
    public ResponseEntity<ApiResponse<PujaBookingResponse>> getBookingById(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        return ResponseEntity.ok(
                new ApiResponse<>(2006, "Booking fetched successfully", 
                        pujaService.getBookingById(id, userId))
        );
    }
}
