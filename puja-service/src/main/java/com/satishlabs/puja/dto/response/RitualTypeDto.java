package com.satishlabs.puja.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Single ritual type for API (e.g. GET /puja/ritual-types). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RitualTypeDto {
    /** Enum name (e.g. GRIHA_PRAVESH). */
    private String value;
    /** Display name (e.g. Griha Pravesh). */
    private String displayName;
}
