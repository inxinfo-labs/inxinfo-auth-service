package com.satishlabs.pandit.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.satishlabs.auth.dto.response.ApiResponse;
import com.satishlabs.pandit.dto.request.PanditFromUserRequest;
import com.satishlabs.pandit.dto.request.PanditRequest;
import com.satishlabs.pandit.dto.response.PanditResponse;
import com.satishlabs.pandit.service.PanditService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/pandit")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class AdminPanditController {

    private final PanditService panditService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PanditResponse>>> getAllPandits() {
        return ResponseEntity.ok(
                new ApiResponse<>(4010, "Pandits fetched successfully", panditService.getAllPanditsForAdmin())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PanditResponse>> getPanditById(@PathVariable Long id) {
        return ResponseEntity.ok(
                new ApiResponse<>(4011, "Pandit fetched successfully", panditService.getPanditById(id))
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PanditResponse>> createPandit(@Valid @RequestBody PanditRequest request) {
        return ResponseEntity.ok(
                new ApiResponse<>(4012, "Pandit created successfully", panditService.createPandit(request))
        );
    }

    /** Admin: approve an existing user (from GET /admin/users) as Pandit. Pass Authorization header. */
    @PostMapping("/from-user")
    public ResponseEntity<ApiResponse<PanditResponse>> createPanditFromUser(
            @Valid @RequestBody PanditFromUserRequest request,
            HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        return ResponseEntity.ok(
                new ApiResponse<>(4015, "User approved as Pandit successfully",
                        panditService.createPanditFromUser(request, authHeader != null ? authHeader : ""))
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PanditResponse>> updatePandit(
            @PathVariable Long id,
            @Valid @RequestBody PanditRequest request) {
        return ResponseEntity.ok(
                new ApiResponse<>(4013, "Pandit updated successfully", panditService.updatePandit(id, request))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePandit(@PathVariable Long id) {
        panditService.deletePandit(id);
        return ResponseEntity.ok(new ApiResponse<>(4014, "Pandit deleted successfully", null));
    }
}
