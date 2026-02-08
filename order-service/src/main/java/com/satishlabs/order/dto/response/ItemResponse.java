package com.satishlabs.order.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String sku;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
