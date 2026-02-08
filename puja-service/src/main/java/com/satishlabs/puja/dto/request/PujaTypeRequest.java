package com.satishlabs.puja.dto.request;

import java.math.BigDecimal;

import com.satishlabs.puja.entity.PujaCategory;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PujaTypeRequest {
    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0", message = "Price must be >= 0")
    private BigDecimal price;

    private String imageUrl;

    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be positive")
    private Integer durationMinutes;

    @NotNull(message = "Category is required")
    private PujaCategory category;

    private Boolean active = true;
}
