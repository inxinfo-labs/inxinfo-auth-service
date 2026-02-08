package com.satishlabs.puja.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.satishlabs.auth.dto.response.ApiResponse;
import com.satishlabs.puja.dto.request.PujaTypeRequest;
import com.satishlabs.puja.dto.response.PujaTypeResponse;
import com.satishlabs.puja.service.PujaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/puja")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class AdminPujaController {

    private final PujaService pujaService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PujaTypeResponse>>> getAllPujaTypes() {
        return ResponseEntity.ok(
                new ApiResponse<>(2010, "Puja types fetched successfully", pujaService.getAllPujaTypesForAdmin())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PujaTypeResponse>> getPujaTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(
                new ApiResponse<>(2011, "Puja type fetched successfully", pujaService.getPujaTypeById(id))
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PujaTypeResponse>> createPujaType(@Valid @RequestBody PujaTypeRequest request) {
        return ResponseEntity.ok(
                new ApiResponse<>(2012, "Puja type created successfully", pujaService.createPujaType(request))
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PujaTypeResponse>> updatePujaType(
            @PathVariable Long id,
            @Valid @RequestBody PujaTypeRequest request) {
        return ResponseEntity.ok(
                new ApiResponse<>(2013, "Puja type updated successfully", pujaService.updatePujaType(id, request))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePujaType(@PathVariable Long id) {
        pujaService.deletePujaType(id);
        return ResponseEntity.ok(new ApiResponse<>(2014, "Puja type deleted successfully", null));
    }
}
