package com.satishlabs.order.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.satishlabs.auth.dto.response.ApiResponse;
import com.satishlabs.order.dto.request.ItemRequest;
import com.satishlabs.order.dto.response.ItemResponse;
import com.satishlabs.order.service.ItemService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/items")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class AdminItemController {

    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ItemResponse>>> getAllItems() {
        return ResponseEntity.ok(
                new ApiResponse<>(5010, "Items fetched successfully", itemService.getAllItems())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ItemResponse>> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(
                new ApiResponse<>(5011, "Item fetched successfully", itemService.getItemById(id))
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ItemResponse>> createItem(@Valid @RequestBody ItemRequest request) {
        return ResponseEntity.ok(
                new ApiResponse<>(5012, "Item created successfully", itemService.createItem(request))
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ItemResponse>> updateItem(
            @PathVariable Long id,
            @Valid @RequestBody ItemRequest request) {
        return ResponseEntity.ok(
                new ApiResponse<>(5013, "Item updated successfully", itemService.updateItem(id, request))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.ok(new ApiResponse<>(5014, "Item deleted successfully", null));
    }
}
