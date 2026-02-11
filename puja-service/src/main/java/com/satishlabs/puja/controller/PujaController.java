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
import com.satishlabs.auth.security.XUserIdPrincipal;
import com.satishlabs.puja.dto.request.PujaBookingRequest;
import com.satishlabs.puja.dto.response.PujaBookingResponse;
import com.satishlabs.puja.dto.response.PujaTypeResponse;
import com.satishlabs.puja.dto.response.RitualTypeDto;
import com.satishlabs.puja.entity.PujaCategory;
import com.satishlabs.puja.entity.RitualType;
import com.satishlabs.puja.service.PujaService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/puja")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class PujaController {

    private final PujaService pujaService;
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

    /** List all ritual types (for Puja catalog and Pandit specializations). */
    @GetMapping("/ritual-types")
    public ResponseEntity<ApiResponse<List<RitualTypeDto>>> getRitualTypes() {
        List<RitualTypeDto> list = java.util.Arrays.stream(RitualType.values())
                .map(r -> RitualTypeDto.builder()
                        .value(r.name())
                        .displayName(r.getDisplayName())
                        .build())
                .toList();
        return ResponseEntity.ok(new ApiResponse<>(2000, "Ritual types fetched successfully", list));
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

    /** List puja types for a ritual type (e.g. SATYANARAYAN_PUJA). For Rituals & Puja detail view. */
    @GetMapping("/ritual-type/{ritualType}")
    public ResponseEntity<ApiResponse<List<PujaTypeResponse>>> getPujaTypesByRitualType(
            @PathVariable String ritualType) {
        try {
            RitualType type = RitualType.valueOf(ritualType.toUpperCase());
            return ResponseEntity.ok(
                    new ApiResponse<>(2007, "Puja types fetched successfully",
                            pujaService.getPujaTypesByRitualType(type))
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
            Authentication authentication,
            HttpServletRequest requestHttp) {
        Long userId = getUserIdFromAuthentication(authentication);
        return ResponseEntity.ok(
                new ApiResponse<>(2004, "Puja booked successfully",
                        pujaService.bookPuja(request, userId, authHeader(requestHttp)))
        );
    }

    @GetMapping("/bookings")
    public ResponseEntity<ApiResponse<List<PujaBookingResponse>>> getUserBookings(
            Authentication authentication,
            HttpServletRequest request) {
        Long userId = getUserIdFromAuthentication(authentication);
        return ResponseEntity.ok(
                new ApiResponse<>(2005, "Bookings fetched successfully",
                        pujaService.getUserBookings(userId, authHeader(request)))
        );
    }

    @GetMapping("/bookings/{id}")
    public ResponseEntity<ApiResponse<PujaBookingResponse>> getBookingById(
            @PathVariable Long id,
            Authentication authentication,
            HttpServletRequest request) {
        Long userId = getUserIdFromAuthentication(authentication);
        return ResponseEntity.ok(
                new ApiResponse<>(2006, "Booking fetched successfully",
                        pujaService.getBookingById(id, userId, authHeader(request)))
        );
    }
}
