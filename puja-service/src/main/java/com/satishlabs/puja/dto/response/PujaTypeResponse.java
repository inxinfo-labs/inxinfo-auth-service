package com.satishlabs.puja.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.satishlabs.puja.entity.PujaCategory;
import com.satishlabs.puja.entity.RitualType;

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
    private RitualType ritualType;
    private PujaCategory category;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
