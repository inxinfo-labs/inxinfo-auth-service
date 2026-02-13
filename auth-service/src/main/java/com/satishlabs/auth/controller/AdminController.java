package com.satishlabs.auth.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.satishlabs.auth.dto.response.SuccessResponse;
import com.satishlabs.auth.dto.response.UserProfileResponse;
import com.satishlabs.auth.entity.Role;
import com.satishlabs.auth.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<SuccessResponse<List<UserProfileResponse>>> getAllUsers() {
        return ResponseEntity.ok(SuccessResponse.of(userService.getAllUsers()));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<SuccessResponse<UserProfileResponse>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(SuccessResponse.of(userService.getProfileById(id)));
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<SuccessResponse<Void>> updateUserRole(
            @PathVariable Long id,
            @RequestBody Role role) {
        userService.updateUserRole(id, role);
        return ResponseEntity.ok(SuccessResponse.of(null));
    }

    @PutMapping("/users/{id}/enable")
    public ResponseEntity<SuccessResponse<Void>> enableUser(@PathVariable Long id) {
        userService.setUserEnabled(id, true);
        return ResponseEntity.ok(SuccessResponse.of(null));
    }

    @PutMapping("/users/{id}/disable")
    public ResponseEntity<SuccessResponse<Void>> disableUser(@PathVariable Long id) {
        userService.setUserEnabled(id, false);
        return ResponseEntity.ok(SuccessResponse.of(null));
    }
}
