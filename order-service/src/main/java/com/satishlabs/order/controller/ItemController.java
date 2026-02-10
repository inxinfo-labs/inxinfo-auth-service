package com.satishlabs.order.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.satishlabs.auth.dto.response.ApiResponse;
import com.satishlabs.order.dto.response.ItemResponse;
import com.satishlabs.order.dto.response.ProductCategoryDto;
import com.satishlabs.order.entity.ProductCategory;
import com.satishlabs.order.service.ItemService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/items")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    /** List product categories (physical items; distinct from Puja Types = ritual services). */
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<ProductCategoryDto>>> getProductCategories() {
        List<ProductCategoryDto> list = java.util.Arrays.stream(ProductCategory.values())
                .map(c -> ProductCategoryDto.builder()
                        .value(c.name())
                        .displayName(c.getDisplayName())
                        .build())
                .toList();
        return ResponseEntity.ok(new ApiResponse<>(5000, "Product categories fetched successfully", list));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ItemResponse>>> getActiveItems() {
        return ResponseEntity.ok(
                new ApiResponse<>(5001, "Items fetched successfully", itemService.getActiveItems())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ItemResponse>> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(
                new ApiResponse<>(5002, "Item fetched successfully", itemService.getItemById(id))
        );
    }
}
