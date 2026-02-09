package com.satishlabs.order.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemRequest {
    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    /** One of: Puja Samagri, Idols & Murtis, Incense & Dhoop, etc. */
    private String category;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0", message = "Price must be >= 0")
    private BigDecimal price;

    @DecimalMin(value = "0", message = "Discount price must be >= 0")
    private BigDecimal discountPrice;

    @NotNull(message = "Stock is required")
    @jakarta.validation.constraints.Min(0)
    private Integer stock = 0;

    /** Comma-separated image URLs */
    private String images;

    private String sku;

    private Boolean active = true;
}
