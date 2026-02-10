package com.satishlabs.order.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.satishlabs.order.entity.ProductCategory;

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
    /** Legacy; when productCategory is set, mirrors its display name. */
    private String category;
    private ProductCategory productCategory;
    private String subCategory;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Integer stock;
    private String images;
    private String sku;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
