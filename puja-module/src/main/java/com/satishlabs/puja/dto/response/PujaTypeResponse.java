package com.satishlabs.puja.dto.response;

import java.math.BigDecimal;

import com.satishlabs.puja.entity.PujaCategory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PujaTypeResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private Integer durationMinutes;
    private PujaCategory category;
    private boolean active;
}
