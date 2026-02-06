package com.satishlabs.auth.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.satishlabs.auth.dto.response.ApiResponse;
import com.satishlabs.auth.dto.response.UserProfileResponse;
import com.satishlabs.auth.entity.Role;
import com.satishlabs.auth.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "http://localhost:3000/")
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserProfileResponse>>> getAllUsers() {
        // This would need a new method in UserService to get all users
        return ResponseEntity.ok(new ApiResponse<>(2001, "Users fetched successfully", List.of()));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserById(@PathVariable Long id) {
        // This would need a new method in UserService
        return ResponseEntity.ok(new ApiResponse<>(2002, "User fetched successfully", null));
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<ApiResponse<Void>> updateUserRole(
            @PathVariable Long id,
            @RequestBody Role role) {
        // This would need a new method in UserService
        return ResponseEntity.ok(new ApiResponse<>(2003, "User role updated successfully", null));
    }

    @PutMapping("/users/{id}/enable")
    public ResponseEntity<ApiResponse<Void>> enableUser(@PathVariable Long id) {
        // This would need a new method in UserService
        return ResponseEntity.ok(new ApiResponse<>(2004, "User enabled successfully", null));
    }

    @PutMapping("/users/{id}/disable")
    public ResponseEntity<ApiResponse<Void>> disableUser(@PathVariable Long id) {
        // This would need a new method in UserService
        return ResponseEntity.ok(new ApiResponse<>(2005, "User disabled successfully", null));
    }
}
